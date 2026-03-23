package com.ruoyi.agriculture.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import com.ruoyi.agriculture.domain.dto.PlanExplanation;
import com.ruoyi.agriculture.domain.dto.PlanExplanationRequest;

/**
 * 解释生成 mock 示例
 *
 * 可以直接运行 main 方法查看农户模式和园艺模式的生成结果。
 */
public class ExplanationGeneratorExamples
{
    public static void main(String[] args)
    {
        ExplanationGenerator generator = new ExplanationGenerator();

        PlanExplanationRequest farmerRequest = buildFarmerMock();
        PlanExplanation farmerExplanation = generator.generate(farmerRequest);

        PlanExplanationRequest gardenRequest = buildGardenMock();
        PlanExplanation gardenExplanation = generator.generate(gardenRequest);

        printCase("FARMER", farmerRequest, farmerExplanation);
        printCase("GARDEN", gardenRequest, gardenExplanation);
    }

    public static PlanExplanationRequest buildFarmerMock()
    {
        PlanExplanationRequest request = new PlanExplanationRequest();
        request.setMode("farmer");
        request.setTotalRevenue(new BigDecimal("16800"));
        request.setTotalCost(new BigDecimal("6400"));
        request.setTotalProfit(new BigDecimal("10400"));
        request.setMajorCrop("玉米");
        request.setCrops(Arrays.asList("玉米", "黄豆", "黄瓜"));
        request.setLandTypes(Arrays.asList("旱地", "平地"));
        request.setBudgetUsedRatio(new BigDecimal("0.93"));
        request.setHasPulseCrop(Boolean.TRUE);
        request.setYieldRate(new BigDecimal("1.63"));

        Map<String, BigDecimal> cropAreas = new LinkedHashMap<String, BigDecimal>();
        cropAreas.put("玉米", new BigDecimal("18"));
        cropAreas.put("黄豆", new BigDecimal("9"));
        cropAreas.put("黄瓜", new BigDecimal("5"));
        request.setCropAreas(cropAreas);
        return request;
    }

    public static PlanExplanationRequest buildGardenMock()
    {
        PlanExplanationRequest request = new PlanExplanationRequest();
        request.setMode("garden");
        request.setUtilityScore(new BigDecimal("86"));
        request.setAestheticScore(new BigDecimal("4.7"));
        request.setSpaceUsageRate(new BigDecimal("0.82"));
        request.setPlantCount(Integer.valueOf(12));
        request.setCrops(Arrays.asList("樱桃番茄", "生菜", "罗勒", "矮生豆"));
        request.setDominantPlants(Arrays.asList("樱桃番茄", "罗勒"));
        request.setLightMatchLevel("good");
        request.setDepthMatchLevel("high");
        request.setContainerTypes(Arrays.asList("种植箱", "立体架"));
        request.setWeightAesthetic(new BigDecimal("0.55"));
        request.setWeightYield(new BigDecimal("0.30"));
        request.setWeightDifficulty(new BigDecimal("0.15"));
        return request;
    }

    private static void printCase(String title, PlanExplanationRequest request, PlanExplanation explanation)
    {
        System.out.println("=== " + title + " INPUT ===");
        System.out.println("mode = " + request.getMode());
        System.out.println("crops = " + request.getCrops());
        System.out.println("majorCrop = " + request.getMajorCrop());
        System.out.println("dominantPlants = " + request.getDominantPlants());
        System.out.println();

        System.out.println("=== " + title + " OUTPUT ===");
        System.out.println("summaryText: " + explanation.getSummaryText());
        System.out.println("reasonText: " + explanation.getReasonText());
        System.out.println("adviceText: " + explanation.getAdviceText());
        System.out.println();
    }
}
