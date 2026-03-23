package com.ruoyi.agriculture.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.ruoyi.agriculture.domain.dto.PlanExplanation;
import com.ruoyi.agriculture.domain.dto.PlanExplanationRequest;

/**
 * 结果解释生成器
 *
 * 通过固定规则 + 条件模板，把结构化方案结果转换为稳定的自然语言说明。
 * 当前先基于 mock 请求对象实现，后续可以直接接真实优化结果组装对象。
 */
public class ExplanationGenerator
{
    private static final BigDecimal RATE_HIGH = new BigDecimal("1.50");

    private static final BigDecimal RATIO_HIGH = new BigDecimal("0.90");

    private static final BigDecimal SCORE_HIGH = new BigDecimal("4.50");

    private static final BigDecimal SPACE_HIGH = new BigDecimal("0.75");

    /**
     * 生成解释
     *
     * @param request 方案输入
     * @return 解释结果
     */
    public PlanExplanation generate(PlanExplanationRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("Plan explanation request must not be null");
        }

        String mode = normalize(request.getMode());
        if ("farmer".equalsIgnoreCase(mode))
        {
            return generateFarmerExplanation(request);
        }
        if ("garden".equalsIgnoreCase(mode))
        {
            return generateGardenExplanation(request);
        }
        throw new IllegalArgumentException("Unsupported explanation mode: " + request.getMode());
    }

    private PlanExplanation generateFarmerExplanation(PlanExplanationRequest request)
    {
        PlanExplanation explanation = new PlanExplanation();

        String majorCrop = firstNonBlank(request.getMajorCrop(), resolveMajorCrop(request.getCropAreas()), "当前主作物");
        List<String> crops = safeList(request.getCrops());
        List<String> auxiliaryCrops = new ArrayList<String>(crops);
        auxiliaryCrops.removeIf(item -> sameText(item, majorCrop));

        StringBuilder summary = new StringBuilder();
        summary.append("该方案以").append(majorCrop).append("为核心");
        if (!auxiliaryCrops.isEmpty())
        {
            summary.append("，结合").append(joinDisplayNames(auxiliaryCrops)).append("进行搭配");
        }
        summary.append("，在当前预算和地块条件下，能够取得较好的整体收益表现。");

        if (isGreaterThan(request.getYieldRate(), RATE_HIGH))
        {
            summary.append("整体收益表现较好，具有较高的种植效益。");
        }
        if (isGreaterThan(request.getBudgetUsedRatio(), RATIO_HIGH))
        {
            summary.append("该方案对预算利用较充分。");
        }
        explanation.setSummaryText(summary.toString());

        List<String> reasons = new ArrayList<String>();
        reasons.add("优先扩大" + majorCrop + "的种植面积，是因为它在当前条件下能够带来更高的收益贡献。");

        if (crops.size() >= 2)
        {
            reasons.add("同时引入" + joinDisplayNames(auxiliaryCrops.isEmpty() ? crops : auxiliaryCrops) + "，有助于平衡收益、空间利用和种植结构。");
        }
        if (Boolean.TRUE.equals(request.getHasPulseCrop()))
        {
            reasons.add("方案中保留了豆类作物配置，有助于兼顾轮作需求与土壤养分平衡。");
        }
        if (safeList(request.getLandTypes()).size() >= 1)
        {
            reasons.add("推荐时已经把地块类型与作物适配关系考虑进去，能尽量避免不合适地块上的错误种植。");
        }

        explanation.setReasonList(reasons);
        explanation.setReasonText(joinParagraphs(reasons));

        List<String> advices = new ArrayList<String>();
        advices.add("建议优先准备" + majorCrop + "所需的种植资源，确保核心收益作物顺利落地。");
        advices.add("若后续预算增加，可优先扩大" + majorCrop + "的种植面积。");
        advices.add("若希望降低投入风险，可适当减少收益波动较大的作物比例。");

        explanation.setAdviceList(advices);
        explanation.setAdviceText(joinParagraphs(advices));
        return explanation;
    }

    private PlanExplanation generateGardenExplanation(PlanExplanationRequest request)
    {
        PlanExplanation explanation = new PlanExplanation();

        List<String> dominantPlants = safeList(request.getDominantPlants());
        if (dominantPlants.isEmpty())
        {
            dominantPlants = trimToSize(safeList(request.getCrops()), 2);
        }
        String plantCombination = dominantPlants.isEmpty() ? "当前植物组合" : joinDisplayNames(dominantPlants);

        StringBuilder summary = new StringBuilder();
        summary.append("该方案优先推荐").append(plantCombination).append("，在当前空间、光照和容器条件下，能够较好兼顾观赏性、收获感与空间利用率。");
        if (isGreaterThan(request.getAestheticScore(), SCORE_HIGH))
        {
            summary.append("整体观赏效果较突出。");
        }
        if (isGreaterThan(request.getSpaceUsageRate(), SPACE_HIGH))
        {
            summary.append("空间利用较充分。");
        }
        explanation.setSummaryText(summary.toString());

        List<String> reasons = new ArrayList<String>();
        if (isPositiveMatch(request.getLightMatchLevel()))
        {
            reasons.add("推荐植物与当前空间的光照条件匹配度较高，更利于正常生长。");
        }
        if (isPositiveMatch(request.getDepthMatchLevel()))
        {
            reasons.add("当前容器深度能够满足所选植物的根系需求，种植可行性较好。");
        }
        if (safeList(request.getCrops()).size() >= 2)
        {
            reasons.add("方案采用了主植物与辅助植物组合的方式，在保证主要观赏或收获目标的同时，提高了整体空间利用率。");
        }

        String dominantPreference = resolveDominantPreference(request);
        if ("aesthetic".equals(dominantPreference))
        {
            reasons.add("当前偏好更倾向观赏性，因此优先保留了视觉表现较好的植物组合。");
        }
        else if ("yield".equals(dominantPreference))
        {
            reasons.add("当前偏好更倾向实际收获，因此提高了可食用植物的推荐比例。");
        }
        else if ("difficulty".equals(dominantPreference))
        {
            reasons.add("当前偏好更关注养护难度，因此组合更偏向管理节奏稳定、维护压力更低的植物。");
        }

        explanation.setReasonList(reasons);
        explanation.setReasonText(joinParagraphs(reasons));

        List<String> advices = new ArrayList<String>();
        advices.add("建议优先使用光照条件较好的位置摆放主植物。");
        advices.add("后续可根据生长情况补充小型香草类植物，提高空间利用率。");
        advices.add("若希望降低养护难度，可减少高维护植物的数量。");

        explanation.setAdviceList(advices);
        explanation.setAdviceText(joinParagraphs(advices));
        return explanation;
    }

    private String resolveMajorCrop(Map<String, BigDecimal> cropAreas)
    {
        if (cropAreas == null || cropAreas.isEmpty())
        {
            return null;
        }
        return cropAreas.entrySet().stream()
            .max(Comparator.comparing(entry -> safeDecimal(entry.getValue())))
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private String resolveDominantPreference(PlanExplanationRequest request)
    {
        BigDecimal aesthetic = safeDecimal(request.getWeightAesthetic());
        BigDecimal yield = safeDecimal(request.getWeightYield());
        BigDecimal difficulty = safeDecimal(request.getWeightDifficulty());

        if (aesthetic.compareTo(yield) >= 0 && aesthetic.compareTo(difficulty) >= 0)
        {
            return "aesthetic";
        }
        if (yield.compareTo(aesthetic) >= 0 && yield.compareTo(difficulty) >= 0)
        {
            return "yield";
        }
        return "difficulty";
    }

    private boolean isPositiveMatch(String level)
    {
        String normalized = normalize(level);
        return "high".equalsIgnoreCase(normalized)
            || "good".equalsIgnoreCase(normalized)
            || "match".equalsIgnoreCase(normalized)
            || "matched".equalsIgnoreCase(normalized)
            || "良好".equals(normalized)
            || "较高".equals(normalized)
            || "高".equals(normalized);
    }

    private boolean isGreaterThan(BigDecimal current, BigDecimal threshold)
    {
        return current != null && current.compareTo(threshold) > 0;
    }

    private BigDecimal safeDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private List<String> safeList(List<String> list)
    {
        return list == null ? Collections.<String>emptyList() : list;
    }

    private String normalize(String text)
    {
        return text == null ? "" : text.trim();
    }

    private boolean sameText(String left, String right)
    {
        return normalize(left).equalsIgnoreCase(normalize(right));
    }

    private String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return "";
        }
        for (String value : values)
        {
            if (!normalize(value).isEmpty())
            {
                return value.trim();
            }
        }
        return "";
    }

    private String joinDisplayNames(List<String> names)
    {
        List<String> filtered = new ArrayList<String>();
        for (String name : names)
        {
            if (!normalize(name).isEmpty())
            {
                filtered.add(name.trim());
            }
        }
        return String.join("、", filtered);
    }

    private List<String> trimToSize(List<String> source, int size)
    {
        if (source.size() <= size)
        {
            return source;
        }
        return new ArrayList<String>(source.subList(0, size));
    }

    private String joinParagraphs(List<String> lines)
    {
        return String.join(" ", lines);
    }

    /**
     * 供调试或日志场景使用的收益率展示。
     */
    @SuppressWarnings("unused")
    private String toPercent(BigDecimal ratio)
    {
        if (ratio == null)
        {
            return "0%";
        }
        return ratio.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP) + "%";
    }
}
