package com.ruoyi.agriculture.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.agriculture.domain.BusContainer;
import com.ruoyi.agriculture.domain.BusCrop;
import com.ruoyi.agriculture.domain.BusCropConfig;
import com.ruoyi.agriculture.domain.BusLand;
import com.ruoyi.agriculture.domain.BusOptimizationTask;
import com.ruoyi.agriculture.domain.BusPlantingResult;
import com.ruoyi.agriculture.domain.BusTaskLand;
import com.ruoyi.agriculture.domain.dto.AgriAlgorithmRequest;
import com.ruoyi.agriculture.domain.dto.AgriAlgorithmResponse;
import com.ruoyi.agriculture.domain.dto.AgriTaskExecuteResult;
import com.ruoyi.agriculture.mapper.BusContainerMapper;
import com.ruoyi.agriculture.mapper.BusCropConfigMapper;
import com.ruoyi.agriculture.mapper.BusCropMapper;
import com.ruoyi.agriculture.mapper.BusLandMapper;
import com.ruoyi.agriculture.mapper.BusOptimizationTaskMapper;
import com.ruoyi.agriculture.mapper.BusPlantingResultMapper;
import com.ruoyi.agriculture.mapper.BusTaskLandMapper;
import com.ruoyi.agriculture.service.IBusOptimizationTaskService;
import com.ruoyi.agriculture.util.PythonProcessExecutor;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 种植优化任务Service业务层处理
 */
@Service
public class BusOptimizationTaskServiceImpl implements IBusOptimizationTaskService
{
    private static final Logger log = LoggerFactory.getLogger(BusOptimizationTaskServiceImpl.class);

    private static final String TASK_STATUS_PENDING = "0";

    private static final String TASK_STATUS_SUCCESS = "1";

    private static final String TASK_STATUS_INFEASIBLE = "2";

    private static final String TASK_STATUS_FAILED = "3";

    private static final BigDecimal MU_TO_SQM = new BigDecimal("666.67");

    @Autowired
    private BusOptimizationTaskMapper busOptimizationTaskMapper;

    @Autowired
    private BusTaskLandMapper busTaskLandMapper;

    @Autowired
    private BusLandMapper busLandMapper;

    @Autowired
    private BusContainerMapper busContainerMapper;

    @Autowired
    private BusCropMapper busCropMapper;

    @Autowired
    private BusCropConfigMapper busCropConfigMapper;

    @Autowired
    private BusPlantingResultMapper busPlantingResultMapper;

    @Autowired
    private PythonProcessExecutor pythonProcessExecutor;

    @Override
    public BusOptimizationTask selectBusOptimizationTaskByTaskId(Long taskId)
    {
        return busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
    }

    @Override
    public List<BusOptimizationTask> selectBusOptimizationTaskList(BusOptimizationTask busOptimizationTask)
    {
        return busOptimizationTaskMapper.selectBusOptimizationTaskList(busOptimizationTask);
    }

    @Override
    public int insertBusOptimizationTask(BusOptimizationTask busOptimizationTask)
    {
        busOptimizationTask.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(busOptimizationTask.getStatus()))
        {
            busOptimizationTask.setStatus(TASK_STATUS_PENDING);
        }
        return busOptimizationTaskMapper.insertBusOptimizationTask(busOptimizationTask);
    }

    @Override
    public int updateBusOptimizationTask(BusOptimizationTask busOptimizationTask)
    {
        return busOptimizationTaskMapper.updateBusOptimizationTask(busOptimizationTask);
    }

    @Override
    public int deleteBusOptimizationTaskByTaskIds(Long[] taskIds)
    {
        return busOptimizationTaskMapper.deleteBusOptimizationTaskByTaskIds(taskIds);
    }

    @Override
    public int deleteBusOptimizationTaskByTaskId(Long taskId)
    {
        return busOptimizationTaskMapper.deleteBusOptimizationTaskByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgriTaskExecuteResult executeTask(Long taskId)
    {
        BusOptimizationTask task = busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
        if (task == null)
        {
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE, "任务不存在", null, 0);
        }

        try
        {
            AgriAlgorithmRequest request = buildAlgorithmRequest(task);
            String inputJson = JSON.toJSONString(request);
            log.info("开始执行优化任务，taskId={}, input={}", taskId, inputJson);

            String outputJson = pythonProcessExecutor.execute(inputJson);
            log.info("优化任务执行完成，taskId={}, output={}", taskId, outputJson);

            AgriAlgorithmResponse response = JSON.parseObject(outputJson, AgriAlgorithmResponse.class);
            return handleAlgorithmResponse(taskId, response);
        }
        catch (Exception ex)
        {
            log.error("执行优化任务失败，taskId={}", taskId, ex);
            cleanupTaskResults(taskId);
            updateTaskStatus(taskId, TASK_STATUS_FAILED);
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE,
                "执行优化任务失败：" + ex.getMessage(), null, 0);
        }
    }

    private AgriAlgorithmRequest buildAlgorithmRequest(BusOptimizationTask task)
    {
        List<BusTaskLand> taskLands = busTaskLandMapper.selectBusTaskLandListByTaskId(task.getTaskId());
        if (StringUtils.isEmpty(taskLands))
        {
            throw new IllegalStateException("任务未关联地块");
        }

        List<Long> landIds = taskLands.stream()
            .map(BusTaskLand::getLandId)
            .distinct()
            .collect(Collectors.toList());
        List<BusLand> lands = busLandMapper.selectBusLandByLandIds(landIds);
        if (StringUtils.isEmpty(lands))
        {
            throw new IllegalStateException("未查询到任务关联地块");
        }

        Map<Long, BusLand> landMap = lands.stream()
            .collect(Collectors.toMap(BusLand::getLandId, item -> item, (left, right) -> left, LinkedHashMap::new));

        List<BusContainer> containers = busContainerMapper.selectBusContainerByLandIds(landIds);
        Map<Long, List<BusContainer>> containerMap = new LinkedHashMap<Long, List<BusContainer>>();
        if (StringUtils.isNotEmpty(containers))
        {
            containerMap = containers.stream()
                .collect(Collectors.groupingBy(BusContainer::getLandId, LinkedHashMap::new, Collectors.toList()));
        }

        List<TaskLandContext> taskLandContexts = new ArrayList<TaskLandContext>();
        for (BusTaskLand taskLand : taskLands)
        {
            BusLand land = landMap.get(taskLand.getLandId());
            if (land == null)
            {
                throw new IllegalStateException("存在无效地块关联，landId=" + taskLand.getLandId());
            }
            List<BusContainer> currentContainers = containerMap.get(taskLand.getLandId());
            if (currentContainers == null)
            {
                currentContainers = Collections.emptyList();
            }
            taskLandContexts.add(new TaskLandContext(land, currentContainers));
        }

        BusCrop cropQuery = new BusCrop();
        List<BusCrop> crops = busCropMapper.selectBusCropList(cropQuery);
        if (StringUtils.isEmpty(crops))
        {
            throw new IllegalStateException("未查询到作物基础数据");
        }

        BusCropConfig cropConfigQuery = new BusCropConfig();
        List<BusCropConfig> cropConfigs = busCropConfigMapper.selectBusCropConfigList(cropConfigQuery);
        if (StringUtils.isEmpty(cropConfigs))
        {
            throw new IllegalStateException("未查询到作物配置数据");
        }

        AgriAlgorithmRequest request = new AgriAlgorithmRequest();
        request.setTaskId(task.getTaskId());
        request.setMode(task.getMode());
        request.setBudget(task.getTotalBudget() == null ? BigDecimal.ZERO : task.getTotalBudget());
        request.setLands(buildLandItems(taskLandContexts));
        request.setContainers(buildContainerItems(taskLandContexts));
        request.setCrops(buildCropItems(crops));
        request.setCropConfigs(buildCropConfigItems(cropConfigs));
        return request;
    }

    private List<AgriAlgorithmRequest.LandItem> buildLandItems(List<TaskLandContext> taskLandContexts)
    {
        List<AgriAlgorithmRequest.LandItem> landItems = new ArrayList<AgriAlgorithmRequest.LandItem>();
        for (TaskLandContext context : taskLandContexts)
        {
            AgriAlgorithmRequest.LandItem item = new AgriAlgorithmRequest.LandItem();
            item.setLandId(context.getLand().getLandId());
            item.setLandCode(context.getLand().getLandCode());
            item.setLandType(context.getLand().getLandType());
            item.setAreaSqm(resolveAreaSqm(context.getLand()));
            item.setLightLevel(context.getLand().getLightLevel());
            landItems.add(item);
        }
        return landItems;
    }

    private List<AgriAlgorithmRequest.ContainerItem> buildContainerItems(List<TaskLandContext> taskLandContexts)
    {
        List<AgriAlgorithmRequest.ContainerItem> containerItems = new ArrayList<AgriAlgorithmRequest.ContainerItem>();
        for (TaskLandContext context : taskLandContexts)
        {
            for (BusContainer container : context.getContainers())
            {
                AgriAlgorithmRequest.ContainerItem item = new AgriAlgorithmRequest.ContainerItem();
                item.setContainerId(container.getContainerId());
                item.setLandId(container.getLandId());
                item.setContainerType(container.getContainerType());
                item.setPlantingSites(container.getPlantingSites());
                item.setDepthCm(container.getDepthCm());
                containerItems.add(item);
            }
        }
        return containerItems;
    }

    private List<AgriAlgorithmRequest.CropItem> buildCropItems(List<BusCrop> crops)
    {
        List<AgriAlgorithmRequest.CropItem> cropItems = new ArrayList<AgriAlgorithmRequest.CropItem>();
        for (BusCrop crop : crops)
        {
            AgriAlgorithmRequest.CropItem item = new AgriAlgorithmRequest.CropItem();
            item.setCropId(crop.getCropId());
            item.setCropName(crop.getCropName());
            item.setIsPulse(crop.getIsPulse());
            cropItems.add(item);
        }
        return cropItems;
    }

    private List<AgriAlgorithmRequest.CropConfigItem> buildCropConfigItems(List<BusCropConfig> cropConfigs)
    {
        List<AgriAlgorithmRequest.CropConfigItem> configItems = new ArrayList<AgriAlgorithmRequest.CropConfigItem>();
        for (BusCropConfig cropConfig : cropConfigs)
        {
            AgriAlgorithmRequest.CropConfigItem item = new AgriAlgorithmRequest.CropConfigItem();
            item.setCropId(cropConfig.getCropId());
            item.setLandType(cropConfig.getLandType());
            item.setYieldVal(cropConfig.getYieldVal());
            item.setCostVal(cropConfig.getCostVal());
            item.setMarketPrice(cropConfig.getMarketPrice());
            item.setUtilityScore(cropConfig.getUtilityScore());
            item.setSunlightReq(cropConfig.getSunlightReq());
            item.setMinDepthReq(cropConfig.getMinDepthReq());
            configItems.add(item);
        }
        return configItems;
    }

    private AgriTaskExecuteResult handleAlgorithmResponse(Long taskId, AgriAlgorithmResponse response)
    {
        if (response == null)
        {
            throw new IllegalStateException("Python返回结果为空");
        }

        Integer status = response.getStatus();
        if (Boolean.TRUE.equals(response.getSuccess()) && Integer.valueOf(1).equals(status))
        {
            replaceTaskResults(taskId, response.getResults());
            updateTaskStatus(taskId, TASK_STATUS_SUCCESS);
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_SUCCESS), Boolean.TRUE,
                defaultMessage(response.getMessage(), "优化任务执行成功"),
                response.getInfeasibleReason(),
                response.getResults() == null ? 0 : response.getResults().size());
        }

        cleanupTaskResults(taskId);
        if (Integer.valueOf(2).equals(status))
        {
            updateTaskStatus(taskId, TASK_STATUS_INFEASIBLE);
            // TODO 持久化 infeasibleReason，当前任务表缺少对应字段。
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_INFEASIBLE), Boolean.FALSE,
                defaultMessage(response.getMessage(), "当前任务无可行解"),
                response.getInfeasibleReason(),
                0);
        }

        updateTaskStatus(taskId, TASK_STATUS_FAILED);
        return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE,
            defaultMessage(response.getMessage(), "优化任务执行失败"),
            response.getInfeasibleReason(),
            0);
    }

    private void replaceTaskResults(Long taskId, List<AgriAlgorithmResponse.ResultItem> results)
    {
        cleanupTaskResults(taskId);
        if (StringUtils.isEmpty(results))
        {
            return;
        }

        Date now = DateUtils.getNowDate();
        for (AgriAlgorithmResponse.ResultItem item : results)
        {
            BusPlantingResult result = new BusPlantingResult();
            result.setTaskId(taskId);
            result.setLandId(item.getLandId());
            result.setContainerId(item.getContainerId());
            result.setCropId(item.getCropId());
            result.setAllocatedAreaSqm(item.getAllocatedAreaSqm());
            result.setDisplayAreaVal(item.getDisplayAreaVal());
            result.setPlantCount(item.getPlantCount());
            result.setProfitAmount(item.getProfitAmount());
            result.setExperienceScore(item.getExperienceScore());
            result.setCreateTime(now);
            busPlantingResultMapper.insertBusPlantingResult(result);
        }
    }

    private void cleanupTaskResults(Long taskId)
    {
        busPlantingResultMapper.deleteBusPlantingResultByTaskId(taskId);
    }

    private void updateTaskStatus(Long taskId, String status)
    {
        BusOptimizationTask task = new BusOptimizationTask();
        task.setTaskId(taskId);
        task.setStatus(status);
        busOptimizationTaskMapper.updateBusOptimizationTask(task);
    }

    private AgriTaskExecuteResult buildExecuteResult(Long taskId, Integer status, Boolean success,
        String message, String infeasibleReason, Integer resultCount)
    {
        AgriTaskExecuteResult result = new AgriTaskExecuteResult();
        result.setTaskId(taskId);
        result.setStatus(status);
        result.setSuccess(success);
        result.setMessage(message);
        result.setInfeasibleReason(infeasibleReason);
        result.setResultCount(resultCount);
        return result;
    }

    private BigDecimal resolveAreaSqm(BusLand land)
    {
        if (land.getAreaSqm() != null)
        {
            return land.getAreaSqm();
        }
        if (land.getInputValue() == null)
        {
            throw new IllegalStateException("地块面积缺失，landId=" + land.getLandId());
        }
        if ("mu".equalsIgnoreCase(land.getUnit()))
        {
            return land.getInputValue().multiply(MU_TO_SQM).setScale(4, RoundingMode.HALF_UP);
        }
        return land.getInputValue().setScale(4, RoundingMode.HALF_UP);
    }

    private String defaultMessage(String message, String defaultMessage)
    {
        return StringUtils.isEmpty(message) ? defaultMessage : message;
    }

    private static class TaskLandContext
    {
        private final BusLand land;

        private final List<BusContainer> containers;

        TaskLandContext(BusLand land, List<BusContainer> containers)
        {
            this.land = land;
            this.containers = containers;
        }

        public BusLand getLand()
        {
            return land;
        }

        public List<BusContainer> getContainers()
        {
            return containers;
        }
    }
}
