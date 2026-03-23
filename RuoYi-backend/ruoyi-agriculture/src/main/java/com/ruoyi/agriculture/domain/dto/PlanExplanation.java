package com.ruoyi.agriculture.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 方案解释输出对象
 *
 * 用于承接规则生成后的解释文案，页面层可以直接渲染
 * summaryText / reasonText / adviceText，也可以按列表逐条展示。
 */
public class PlanExplanation
{
    private String summaryText;

    private String reasonText;

    private String adviceText;

    private List<String> reasonList = new ArrayList<String>();

    private List<String> adviceList = new ArrayList<String>();

    public String getSummaryText()
    {
        return summaryText;
    }

    public void setSummaryText(String summaryText)
    {
        this.summaryText = summaryText;
    }

    public String getReasonText()
    {
        return reasonText;
    }

    public void setReasonText(String reasonText)
    {
        this.reasonText = reasonText;
    }

    public String getAdviceText()
    {
        return adviceText;
    }

    public void setAdviceText(String adviceText)
    {
        this.adviceText = adviceText;
    }

    public List<String> getReasonList()
    {
        return reasonList;
    }

    public void setReasonList(List<String> reasonList)
    {
        this.reasonList = reasonList;
    }

    public List<String> getAdviceList()
    {
        return adviceList;
    }

    public void setAdviceList(List<String> adviceList)
    {
        this.adviceList = adviceList;
    }
}
