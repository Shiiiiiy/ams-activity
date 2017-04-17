package com.uws.activity.dao;

import java.util.List;
import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.activity.ActivityBaseinfoModel;
import com.uws.domain.activity.ActivityMemberModel;
import com.uws.domain.activity.ActivityTeacherModel;
import com.uws.domain.activity.ActivityWorkerModel;

/**
 * 活动管理数据层接口
 */
public interface IActivityManageDao extends IBaseDao {

	/**
	 * 【活动信息维护】
	 *  分页查询当前用户活动信息
	 * @param activity			活动对象
	 * @param pageNo			当前页码
	 * @param pageSize		    分页大小
	 * @param userId			当前用户
	 * @return 分页信息
	 */
	public Page queryManageActivityPage(ActivityBaseinfoModel activity, int pageNo, int pageSize, String userId);

	/**
	 * 【审核信息列表】
	 * @param pageNo
	 * @param pageSize
	 * @param activity
	 * @param userId        用户id
	 * @param objectIds     已申请的校内活动id
	 * @param objectIds1         已申请的校外活动id
	 * @return 分页信息
	 */
	public Page queryApproveActivityPage(int pageNo, int pageSize, ActivityBaseinfoModel activity, String userId, String[] objectIds, String[] objectIds1, String[] objectIds2, String[] objectIds3, String[] objectIds4);

	/**
	 * 【报名信息列表】
	 * @param activity
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @param collegeId
	 * @param objectIds
	 * @param objectIds1
	 * @param volunteer      是否为志愿者
	 * @param leagueMember   是否为团员
	 * @return 分页信息
	 */
	public Page querySignUpActivityPage(ActivityBaseinfoModel activity, int pageNo, int pageSize, String userId, String collegeId, 
			String[] objectIds, String[] objectIds1, String volunteer, String leagueMember);

	/**
	 * 【活动信息维护】
	 *  
	 * @param activityMember 活动参与人员对象
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @return 分页信息
	 */
	public Page querySelfActivityPage(ActivityMemberModel activityMember, int pageNo, int pageSize, String userId);

	/**
	 * 【活动信息维护】
	 *  删除活动信息【逻辑删除】
	 * @param objectId      业务主键
	 */
	public void deleteActivity(String objectId);

	/**
	 * 【活动信息维护】
	 * 修改活动信息的审核信息
	 * @param activity      活动实体类
	 */
	public void updateApproveActivity(ActivityBaseinfoModel activity);

	/**
	 * 【活动信息维护】
	 *  修改活动报名状态
	 * @param objectId
	 * @param isSignStatus  报名状态
	 */
	public void updateActivitySignStatus(String objectId, String isSignStatus);

	/**
	 * 【活动信息维护】
	 *  添加活动总结
	 * @param activity
	 */
	public void addActivitySummary(ActivityBaseinfoModel activity);

	/**
	 * 【活动信息维护】
	 *  修改活动状态
	 * @param objectId
	 * @param status 
	 */
	public void updateActivityStatus(String objectId, String status);

	/**
	 * 【活动信息维护】
	 *  活动名称的惟一性验证
	 * @param activityName    活动名称
	 * @param activityId      活动id
	 * @return
	 */
	public ActivityBaseinfoModel queryActivityByActivityName(String activityName, String activityId);

	
	/**
	 * 【活动参与人员维护】
	 *  查询活动参与人员列表
	 * @param pageNo
	 * @param pageSize
	 * @param activityMember   活动参与人员对象
	 * @param activityId
	 * @return
	 */
	public Page queryActivityMemberPage(int pageNo, int pageSize, ActivityMemberModel activityMember, String activityId);

	/**
	 * 【活动参与人员维护】
	 *  删除活动参与人员
	 * @param activityId
	 * @param studentId
	 */
	public void deleteActivityMember(String activityId, String studentId);

	/**
	 * 【活动参与人员维护】
	 *  修改活动参与人员的审核状态
	 * @param activityId
	 * @param memberId
	 * @param approveStatus     审核状态
	 * @param suggest           审核意见
	 */
	public void updateActivityMember(String activityId, String memberId,
			String approveStatus, String suggest);

	/**
	 *【活动参与人员维护】
	 * 根据活动id学生id查询学生是否参加了该活动
	 * @param activityId
	 * @param studentId
	 * @return
	 */
	public ActivityMemberModel getActivityMember(String activityId,String studentId);

	/**
	 *【活动参与人员维护】
	 * 活动参与人员信息列表
	 * @param activityId
	 * @return
	 */
	public List<ActivityMemberModel> queryActivityMemberList(String activityId);

	/**
	 * 【活动参与人员维护】
	 *  活动参与人员信息列表(线下报名)
	 * @param activityId
	 * @return
	 */
	public List<ActivityMemberModel> queryOffActivityMemberList(
			String activityId);

	/**
	 * 【活动参与人员维护】
	 *  活动已通过参与人员信息列表
	 * @param activityId
	 * @return
	 */
	public List<ActivityMemberModel> queryActivityPassMemberList(
			String activityId);

	/**
	 * 【活动参与人员维护】
	 *  根据活动id删除所用参与人员
	 *  @param activityId
	 */
	public void deleteMemberByActivityId(String activityId);

	/**
	 * 【活动参与人员维护】
	 *  查询当前登录人已报名或已申请的活动信息列表
	 * @param userId
	 * @return
	 */
	public List<ActivityBaseinfoModel> queryActivityByMemberIdList(String userId);

	/**
	 * 【活动工作人员维护】
	 *  根据活动id删除所有活动工作人员
	 * @param activityId
	 */
	public void deleteActivityWorker(String activityId);

	/**
	 *【活动工作人员维护】
	 * 根据业务主键分页查询活动信息
	 * @param pageNo
	 * @param pageSize
	 * @param activityId
	 * @return
	 */
	public Page queryActivityWorkersPage(int pageNo, int pageSize,String activityId);

	/**
	 * 根据业务主键分页查询活动工作人员
	 * @param pageNo
	 * @param pageSize
	 * @param activityId
	 * @return
	 */
	public Page queryActivityTeacherPage(int pageNo, int pageSize,String activityId);
	
	/**
	 *【活动工作人员维护】
	 * 根据业务主键查询工作人员列表
	 * @param activityId
	 * @return
	 */
	public List<ActivityWorkerModel> queryActivityWorkerList(String activityId);

	/**
	 *【活动带队老师维】
	 * 根据业务主键删除活动带队老师
	 * @param activityId
	 */
	public void deleteActivityTeacher(String activityId);

	/**
	 *【活动带队老师维】
	 * 活动带队老师信息列表
	 * @param activityId
	 * @return
	 */
	public List<ActivityTeacherModel> queryActivityTeacherList(String activityId);

	/**
	 *【活动监管分析】
	 * 活动管理监管分析查询列表
	 * @param activity
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page queryStatisticActivityPage(ActivityBaseinfoModel activity, int pageNo, int pageSize);

}
