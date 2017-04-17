package com.uws.activity.service;

import java.util.List;
import java.util.Map; 

import com.uws.apw.model.ApproveResult; 
import com.uws.core.base.IBaseService; 
import com.uws.core.hibernate.dao.support.Page;  
import com.uws.domain.activity.ActivityBaseinfoModel;
import com.uws.domain.activity.ActivityMemberModel;
import com.uws.domain.activity.ActivityTeacherModel;
import com.uws.domain.activity.ActivityWorkerModel; 
import com.uws.user.model.User; 

/** 
* @ClassName:  IActivityManageService
* @Description: 活动管理Service
* @author
* @date
*/
public interface IActivityManageService extends IBaseService{
	/** 
	* @Title: queryApproveActivityPage 
	* @Description: 维护信息列表
	* @param activity 活动对象
	* @param pageNo 页数
	* @param pageSize 每页显示条数  
	* @param userId 用户id
	* @return Page    
	*/
	public Page queryManageActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize, String userId);
	/** 
	* @Title: queryApproveActivityPage 
	* @Description: 审核信息列表
	* @param pageNo 页数
	* @param pageSize 每页显示条数 
	* @param activity 活动对象
	* @param userId 用户id
	* @param objectIds 已申请的校内活动id
	* @param objectIds1 已申请的校外活动id
	* @return Page    
	* @throws 
	*/
	public Page queryApproveActivityPage(int pageNo, int pageSize,ActivityBaseinfoModel activity, String userId,String[] objectIds, String[] objectIds1, String[] objectIds2, String[] objectIds3, String[] objectIds4);
	/** 
	* @Title: querySignUpActivityPage 
	* @Description: 报名信息列表
	* @param activity
    * @param pageNo 页数
	* @param pageSize 每页显示条数 
	* @param userId 用户id
	* @param collegeId 学院id
	* @param objectIds 已申请的活动id
	* @param objectIds1 已参与的社团列表活动id
	* @param volunteer 是否为志愿者标记字段
	* @param leagueMember 是否为团员标记字段
	* @return Page    
	*/
	public Page querySignUpActivityPage(ActivityBaseinfoModel activity, int pageNo, int pageSize, String userId, String collegeId, String[] objectIds, String[] objectIds1, String volunteer, String leagueMember);
	/** 
	* @Title: querySelfActivityPage 
	* @Description: 我的活动信息列表
	* @param activityMember 活动参与人员对象
    * @param pageNo 页数
	* @param pageSize 每页显示条数 
	* @param userId 用户id
	* @return Page    
	*/
	public Page querySelfActivityPage(ActivityMemberModel activityMember, int pageNo,int pageSize, String userId);
	/** 
	* @Title: saveActivity 
	* @Description: 保存活动信息，要先删除活动工作人员信息，再添加工作人员信息
	* @param activity 活动对象
	* @param workerIds 工作人员ids
	* @param teacherIds 带队教师ids   
	*/
	public void saveActivity(ActivityBaseinfoModel activity,String workerIds,String teacherIds);
	/** 
	* @Title: updateActivity 
	* @Description: 修改活动信息（包括活动工作人员，带队教师）
	* @param activity 活动对象
	* @param workerIds 工作人员ids
	* @param teacherIds 带队教师ids 
	*/
	public void updateActivity(ActivityBaseinfoModel activity,String workerIds,String teacherIds);
	/** 
	* @Title: updateActivityInfo 
	* @Description: 修改活动基本信息
	* @param activity 活动对象   
	*/
	public void updateActivityInfo(ActivityBaseinfoModel activity);
	/** 
	* @Title: updateApproveActivity 
	* @Description: 修改审核信息
	* @param activity 活动对象  
	*/
	public void updateApproveActivity(ActivityBaseinfoModel activity);
	/** 
	* @Title: deleteActivity 
	* @Description: 删除
	* @param id 活动对象id
	*/
	public void deleteActivity(String id);
	/** 
	* @Title: queryActivity 
	* @Description: 查询（根据主键id）
	* @param id 活动对象id 
	* @return ActivityBaseinfoModel 活动对象
	*/
	public ActivityBaseinfoModel queryActivity(String id);
	/**
	 * @Title: saveActivityApproveResult
	 * @Description: 保存活动信息审批结果
	 * @param objectId 活动id
	 * @param ApproveResult 审核状态
	 * @param nextApproverId 下一节点人
	 * @param approveReason 审核意见
	 */
	public ApproveResult saveActivityApproveResult(String objectId, ApproveResult result, String nextApproverId, String approveReason);
	/**
	 * @Title: submitApprove
	 * @Description: 审批 修改状态 添加下一节点人
	 * @param objectId
	 * @param initiator 申请人
	 * @param nextApprover 下一节点审核人
	 * @return ApproveResult 审核状态
	 */
	public ApproveResult submitApprove(String objectId,User initiator,User nextApprover);
	/** 
	* @Title: updateActivitySignStatus 
	* @Description: 修改报名状态
	* @param objectId 活动id
	* @param isSignStatus 报名状态   
	*/
	public void updateActivitySignStatus(String objectId, String isSignStatus);
	/** 
	* @Title: updateActivitySummary 
	* @Description: 添加活动总结
	* @param activity 活动对象
	* @param fileId 上传文件id 
	*/
	public void updateActivitySummary(ActivityBaseinfoModel activity, String[] fileId);
    /** 
	* @Title: queryActivityByActivityName 
	* @Description: 活动名称的惟一性验证
	* @param activityName 活动名称
	* @param activityId 活动id
	*/
	public ActivityBaseinfoModel queryActivityByActivityName(String activityName, String activityId);
	/** 
	* @Title: queryStatisticActivityPage 
	* @Description: 活动管理监管分析查询列表
	* @param activity 活动实体
	* @param pageNo 页数
	* @param pageSize 每页显示条数
	* @return Page    
	*/
	public Page queryStatisticActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize);
	/*----------参与人员维护------------*/
	/** 
	* @Title: getActivityMember
	* @Description: 查询学生是否参加该活动
	* @param activityId 活动id
	* @param studentId 学生id
	* @return ActivityMemberModel    
	*/
	public ActivityMemberModel getActivityMember(String activityId, String studentId);
	/** 
	* @Title: saveActivityMember
	* @Description: 保存活动参与人员
	* @param activityMemberModel 活动参与人员对象   
	* @return String    
	*/
	public String saveActivityMember(ActivityMemberModel activityMemberModel);
	/** 
	* @Title: saveActivityMembers 
	* @Description: 保存活动参与人员（多个）
	* @param activityId 活动id
	* @param studentIds 学生id
	*/
	public void saveActivityMembers(String activityId, String studentIds);
	/** 
	* @Title: deleteActivityAllMember 
	* @Description: 删除所用活动参与人员
	* @param  @param activity    
	*/
	public void deleteActivityAllMember(String activityId);
	/** 
	* @Title: queryActivityMemberPage 
	* @Description: 活动参与人员列表
	* @param activityMember
	* @param pageNo
	* @param pageSize
    * @param activityId   
	* @return Page    
	*/
	public Page queryActivityMemberPage(int pageNo, int pageSize, ActivityMemberModel activityMember, String activityId);
	/** 
	* @Title: deleteActivityMembers 
	* @Description: 删除活动参与人员
	* @param activity
	* @param ids
	*/
	public void deleteActivityMembers(String activityId, String ids);
	/** 
	* @Title: updateActivityMembers 
	* @Description: 修改活动参与人员的审核状态
	* @param activity 活动对象
	* @param ids 参与人员id
	* @param approveStatus 审核状态
	* @param suggest 审核意见
	* @return String    
	*/
	public String updateActivityMembers(String activityId, String ids, String approveStatus, String suggest);
	/** 
	 * @Title: importActivityMember
	 * @Description: 导入活动参与学生列表
	 * @param filePath
	 * @param importId
	 * @param initDate
	 * @param c
	 * @param activityId
	 * @return String
	 */
	public String importActivityMember(String filePath, String importId, Map initDate, Class c, String activityId) throws Exception;
	/** 
	* @Title: queryActivityMemberList 
	* @Description: 活动参与人员信息列表
	* @param activityId
	* @return List<ActivityMemberModel>    
	*/
	public List<ActivityMemberModel> queryActivityMemberList(String activityId);
	/** 
	* @Title: queryOffActivityMemberList 
	* @Description: 活动参与人员信息列表(线下报名)
	* @param activityId
	* @return List<ActivityMemberModel>    
	*/
	public List<ActivityMemberModel> queryOffActivityMemberList(String activityId);
	/** 
	* @Title: queryActivityPassMemberList 
	* @Description: 活动已通过参与人员信息列表
	* @param activityId
	* @return List<ActivityMemberModel> 
	*/
	public List<ActivityMemberModel> queryActivityPassMemberList(String activityId);
	/** 
	* @Title: queryActivityByMemberIdList
	* @Description: 查询当前登录人已报名或已申请的活动信息列表
	* @param userId  
	* @return String[]    
	*/
	public String[] queryActivityByMemberIdList(String userId);
	/*----------工作人员维护------------*/
	
	/** 
	* @Title: queryActivityWorkerList 
	* @Description: 根据活动id查询活动工作人员列表
	* @param activityId   
	* @return List<ActivityWorkerModel>  
	*/
	public List<ActivityWorkerModel> queryActivityWorkerList(String activityId);
	/** 
	* @Title: queryActivityWorkers 
	* @Description: 根据活动id查询活动工作人员page
	* @param pageNo
	* @param pageSize 
	* @param activityId 
	* @return Page  
	*/
	public Page queryActivityWorkersPage(int pageNo,int pageSize,String activityId);
	/** 
	* @Title: deleteActivityWorkers 
	* @Description: 根据活动id删除所有活动工作人员
	* @param activityId    
	*/
	public void deleteActivityWorkers(String activityId);
	/*----------带队教师维护------------*/
	/** 
	* @Title: queryActivityTeacherList 
	* @Description: 活动带队老师信息列表
	* @param activityId
	* @return List<ActivityTeacherModel>    
	*/
	public List<ActivityTeacherModel> queryActivityTeacherList(String activityId);
	/** 
	* @Title: queryActivityTeacherPage 
	* @Description: 根据活动id查询活动带队教师page
	* @param pageNo
	* @param pageSize
	* @param activityId
	* @return Page
	*/
	public Page queryActivityTeacherPage(int pageNo,int pageSize,String activityId);
	/** 
	* @Title: deleteActivityTeachers 
	* @Description: 根据活动id删除所有活动带队教师
	* @param activityId    
	*/
	public void deleteActivityTeachers(String activityId);
	/**
	 * 根据id获取学院、专业、班级的字符串
	 * @param ids
	 * @param type
	 * @return
	 */
	public String getAllNameStrByIds(String ids,String type);
}
