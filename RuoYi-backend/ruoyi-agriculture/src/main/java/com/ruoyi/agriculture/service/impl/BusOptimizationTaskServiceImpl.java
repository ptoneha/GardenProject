package com.ruoyi.agriculture.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.agriculture.constant.AgriErrorCode;
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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

@Service
public class BusOptimizationTaskServiceImpl implements IBusOptimizationTaskService
{
    private static final Logger log = LoggerFactory.getLogger(BusOptimizationTaskServiceImpl.class);

    private static final String TASK_STATUS_PENDING = "0";

    private static final String TASK_STATUS_SUCCESS = "1";

    private static final String TASK_STATUS_INFEASIBLE = "2";

    private static final String TASK_STATUS_FAILED = "3";

    private static final String TASK_STATUS_RUNNING = "4";

    private static final BigDecimal MU_TO_SQM = new BigDecimal("666.67");

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal ONE = BigDecimal.ONE;

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
        BusOptimizationTask task = busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
        return canAccessTask(task) ? task : null;
    }

    @Override
    public List<BusOptimizationTask> selectBusOptimizationTaskList(BusOptimizationTask busOptimizationTask)
    {
        applyTaskDataScope(busOptimizationTask);
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
        if (busOptimizationTask.getOwnerUserId() == null)
        {
            busOptimizationTask.setOwnerUserId(resolveCurrentUserId());
        }
        return busOptimizationTaskMapper.insertBusOptimizationTask(busOptimizationTask);
    }

    @Override
    public int updateBusOptimizationTask(BusOptimizationTask busOptimizationTask)
    {
        if (!canAccessTask(busOptimizationTask.getTaskId()))
        {
            return 0;
        }
        return busOptimizationTaskMapper.updateBusOptimizationTask(busOptimizationTask);
    }

    @Override
    public int deleteBusOptimizationTaskByTaskIds(Long[] taskIds)
    {
        int rows = 0;
        for (Long taskId : taskIds)
        {
            rows += deleteBusOptimizationTaskByTaskId(taskId);
        }
        return rows;
    }

    @Override
    public int deleteBusOptimizationTaskByTaskId(Long taskId)
    {
        if (!canAccessTask(taskId))
        {
            return 0;
        }
        return busOptimizationTaskMapper.deleteBusOptimizationTaskByTaskId(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgriTaskExecuteResult executeTask(Long taskId)
    {
        BusOptimizationTask task = busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId);
        if (task == null)
        {
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE, "error",
                AgriErrorCode.ERR_TASK_NOT_FOUND.getCode(), 0);
        }
        if (!canExecuteTask(task))
        {
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE, "error",
                AgriErrorCode.ERR_TASK_FORBIDDEN.getCode(), 0);
        }
        if (!lockTaskForExecution(task))
        {
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE, "error",
                AgriErrorCode.ERR_TASK_RUNNING.getCode(), 0);
        }

        try
        {
            validateTaskRules(task);

            AgriAlgorithmRequest request = buildAlgorithmRequest(task);
            String inputJson = JSON.toJSONString(request);
            log.info("Start agriculture task execution, taskId={}, input={}", taskId, inputJson);

            String outputJson = pythonProcessExecutor.execute(inputJson);
            log.info("Agriculture task execution finished, taskId={}, output={}", taskId, outputJson);

            AgriAlgorithmResponse response;
            try
            {
                response = JSON.parseObject(outputJson, AgriAlgorithmResponse.class);
            }
            catch (Exception ex)
            {
                throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_BAD_RESPONSE.getCode(), ex);
            }
            return handleAlgorithmResponse(taskId, response);
        }
        catch (Exception ex)
        {
            log.error("Agriculture task execution failed, taskId={}", taskId, ex);
            cleanupTaskResults(taskId);
            updateTaskStatus(taskId, TASK_STATUS_FAILED);
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE, "error",
                resolveErrorCode(ex), 0);
        }
    }

    private void validateTaskRules(BusOptimizationTask task)
    {
        BigDecimal minPulseRatio = task.getMinPulseRatio();
        if (minPulseRatio != null && (minPulseRatio.compareTo(ZERO) < 0 || minPulseRatio.compareTo(ONE) > 0))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_INVALID_INPUT.getCode());
        }

        Set<Long> whitelist = new LinkedHashSet<Long>(parseCropIdList(task.getCropWhitelist()));
        Set<Long> blacklist = new LinkedHashSet<Long>(parseCropIdList(task.getCropBlacklist()));
        whitelist.retainAll(blacklist);
        if (!whitelist.isEmpty())
        {
            throw new IllegalStateException(AgriErrorCode.ERR_CONFLICTING_CROP_RULES.getCode());
        }
    }

    private boolean canExecuteTask(BusOptimizationTask task)
    {
        if (task == null)
        {
            return false;
        }
        if (task.getOwnerUserId() == null)
        {
            return true;
        }

        Long currentUserId = resolveCurrentUserId();
        if (currentUserId == null)
        {
            return false;
        }
        return SecurityUtils.isAdmin(currentUserId) || currentUserId.equals(task.getOwnerUserId());
    }

    private void applyTaskDataScope(BusOptimizationTask task)
    {
        if (task == null || isCurrentAdmin())
        {
            return;
        }
        task.setOwnerUserId(resolveCurrentUserId());
    }

    private boolean canAccessTask(Long taskId)
    {
        return canAccessTask(busOptimizationTaskMapper.selectBusOptimizationTaskByTaskId(taskId));
    }

    private boolean canAccessTask(BusOptimizationTask task)
    {
        if (task == null)
        {
            return false;
        }
        if (isCurrentAdmin())
        {
            return true;
        }
        Long currentUserId = resolveCurrentUserId();
        return currentUserId != null && currentUserId.equals(task.getOwnerUserId());
    }

    private boolean lockTaskForExecution(BusOptimizationTask task)
    {
        if (TASK_STATUS_RUNNING.equals(task.getStatus()))
        {
            return false;
        }
        String fromStatus = StringUtils.isEmpty(task.getStatus()) ? TASK_STATUS_PENDING : task.getStatus();
        int rows = busOptimizationTaskMapper.updateTaskStatusWithLock(task.getTaskId(), fromStatus, TASK_STATUS_RUNNING);
        return rows > 0;
    }

    private AgriAlgorithmRequest buildAlgorithmRequest(BusOptimizationTask task)
    {
        List<BusTaskLand> taskLands = busTaskLandMapper.selectBusTaskLandListByTaskId(task.getTaskId());
        if (StringUtils.isEmpty(taskLands))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_NO_LANDS.getCode());
        }

        List<Long> landIds = taskLands.stream()
            .map(BusTaskLand::getLandId)
            .distinct()
            .collect(Collectors.toList());
        List<BusLand> lands = busLandMapper.selectBusLandByLandIds(landIds);
        if (StringUtils.isEmpty(lands))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_NO_LANDS.getCode());
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
                throw new IllegalStateException(AgriErrorCode.ERR_NO_LANDS.getCode());
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
            throw new IllegalStateException(AgriErrorCode.ERR_NO_CROPS.getCode());
        }

        BusCropConfig cropConfigQuery = new BusCropConfig();
        List<BusCropConfig> cropConfigs = busCropConfigMapper.selectBusCropConfigList(cropConfigQuery);
        if (StringUtils.isEmpty(cropConfigs))
        {
            throw new IllegalStateException(AgriErrorCode.ERR_NO_CROP_CONFIGS.getCode());
        }

        AgriAlgorithmRequest request = new AgriAlgorithmRequest();
        request.setTaskId(task.getTaskId());
        request.setMode(task.getMode());
        request.setBudget(task.getTotalBudget() == null ? ZERO : task.getTotalBudget());
        request.setTaskSeason(task.getTaskSeason());
        request.setMinPulseRatio(task.getMinPulseRatio() == null ? ZERO : task.getMinPulseRatio());
        request.setOwnerUserId(task.getOwnerUserId());
        request.setCropWhitelist(parseCropIdList(task.getCropWhitelist()));
        request.setCropBlacklist(parseCropIdList(task.getCropBlacklist()));
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
            item.setStartMonth(cropConfig.getStartMonth());
            item.setEndMonth(cropConfig.getEndMonth());
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
            throw new IllegalStateException(AgriErrorCode.ERR_PYTHON_BAD_RESPONSE.getCode());
        }

        Integer status = response.getStatus();
        if (Boolean.TRUE.equals(response.getSuccess()) && Integer.valueOf(1).equals(status))
        {
            replaceTaskResults(taskId, response.getResults());
            updateTaskStatus(taskId, TASK_STATUS_SUCCESS);
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_SUCCESS), Boolean.TRUE,
                defaultMessage(response.getMessage(), "ok"),
                response.getInfeasibleReason(),
                response.getResults() == null ? 0 : response.getResults().size());
        }

        cleanupTaskResults(taskId);
        if (Integer.valueOf(2).equals(status))
        {
            updateTaskStatus(taskId, TASK_STATUS_INFEASIBLE);
            return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_INFEASIBLE), Boolean.FALSE,
                defaultMessage(response.getMessage(), "infeasible"),
                response.getInfeasibleReason(),
                0);
        }

        updateTaskStatus(taskId, TASK_STATUS_FAILED);
        return buildExecuteResult(taskId, Integer.valueOf(TASK_STATUS_FAILED), Boolean.FALSE,
            defaultMessage(response.getMessage(), "error"),
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
        String normalizedReasonCode = AgriErrorCode.normalizeCode(infeasibleReason);
        String reasonDescription = AgriErrorCode.resolveDescription(normalizedReasonCode);

        AgriTaskExecuteResult result = new AgriTaskExecuteResult();
        result.setTaskId(taskId);
        result.setStatus(status);
        result.setSuccess(success);
        result.setMessage(resolveDisplayMessage(message, reasonDescription));
        result.setInfeasibleReason(normalizedReasonCode);
        result.setInfeasibleReasonDesc(reasonDescription);
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
            throw new IllegalStateException(AgriErrorCode.ERR_INVALID_INPUT.getCode());
        }
        if ("mu".equalsIgnoreCase(land.getUnit()))
        {
            return land.getInputValue().multiply(MU_TO_SQM).setScale(4, RoundingMode.HALF_UP);
        }
        return land.getInputValue().setScale(4, RoundingMode.HALF_UP);
    }

    private List<Long> parseCropIdList(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<Long>();
        String[] parts = raw.split(",");
        for (String part : parts)
        {
            String value = part == null ? null : part.trim();
            if (StringUtils.isEmpty(value))
            {
                continue;
            }
            try
            {
                ids.add(Long.valueOf(value));
            }
            catch (NumberFormatException ex)
            {
                throw new IllegalStateException(AgriErrorCode.ERR_INVALID_INPUT.getCode(), ex);
            }
        }
        return ids;
    }

    private Long resolveCurrentUserId()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception ex)
        {
            log.debug("No authenticated user found when resolving task owner", ex);
            return null;
        }
    }

    private boolean isCurrentAdmin()
    {
        Long userId = resolveCurrentUserId();
        return userId != null && SecurityUtils.isAdmin(userId);
    }

    private String defaultMessage(String message, String defaultMessage)
    {
        return StringUtils.isEmpty(message) ? defaultMessage : message;
    }

    private String resolveErrorCode(Exception ex)
    {
        if (ex == null)
        {
            return AgriErrorCode.ERR_INTERNAL.getCode();
        }

        String message = ex.getMessage();
        if (StringUtils.isNotEmpty(message))
        {
            return AgriErrorCode.normalizeCode(message);
        }
        return AgriErrorCode.ERR_INTERNAL.getCode();
    }

    private String resolveDisplayMessage(String message, String reasonDescription)
    {
        if (StringUtils.isNotEmpty(reasonDescription))
        {
            if (StringUtils.isEmpty(message)
                || "infeasible".equalsIgnoreCase(message)
                || "error".equalsIgnoreCase(message))
            {
                return reasonDescription;
            }
        }
        return message;
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
