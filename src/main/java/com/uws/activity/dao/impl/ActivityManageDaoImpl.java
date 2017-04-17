package com.uws.activity.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.activity.dao.IActivityManageDao;
import com.uws.common.util.Constants;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.activity.ActivityBaseinfoModel;
import com.uws.domain.activity.ActivityMemberModel;
import com.uws.domain.activity.ActivityTeacherModel;
import com.uws.domain.activity.ActivityWorkerModel;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.util.ProjectConstants;

@Repository("com.uws.activity.dao.impl.ActivityManageDaoImpl")
public class ActivityManageDaoImpl extends BaseDaoImpl implements IActivityManageDao {
	// 数据字典工具类
	private static DicUtil dicUtil=DicFactory.getDicUtil();
 
	/**----- 活动信息维护-------*/
	/** 
	* @Title: queryManageActivityPage 
	* @Description: 活动维护列表
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public Page queryManageActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize, String userId) {
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("from ActivityBaseinfoModel a where 1=1 ");
		if(!StringUtils.isEmpty(userId)){
			hql.append(" and a.creator.id = :creatorId ");
			values.put("creatorId", userId);
		}
		//查询条件是否存在
		if(null != activity){
			//活动名称
			if(!StringUtils.isEmpty(activity.getActivityName())){
				hql.append(" and a.activityName like :activityName ");
				if (HqlEscapeUtil.IsNeedEscape(activity.getActivityName())){
					values.put("activityName", "%" + HqlEscapeUtil.escape(activity.getActivityName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else{
					values.put("activityName", "%" +activity.getActivityName()+ "%");
				}
			}
			//活动类型
			if(null!=activity.getActivityType() && !StringUtils.isEmpty(activity.getActivityType().getId())){
				hql.append(" and a.activityType.id = :activityTypeId");
				values.put("activityTypeId", activity.getActivityType().getId());
			}
			//活动类别
			if(null!=activity.getActivityCategory() && !StringUtils.isEmpty(activity.getActivityCategory().getId())){
				hql.append(" and a.activityCategory.id = :activityCategoryId  ");
				values.put("activityCategoryId",activity.getActivityCategory().getId());
			}
			//活动级别
			if(null!=activity.getActivityLevel() && !StringUtils.isEmpty(activity.getActivityLevel().getId())){
				hql.append(" and a.activityLevel.id = :activityLevelId");
				values.put("activityLevelId",activity.getActivityLevel().getId());
			}
			//学院单位
			if (null != activity.getCollegeIds() && !StringUtils.isEmpty(activity.getCollegeIds())){
				hql.append(" and a.collegeIds like :collegeIds");
				values.put("collegeIds", "%" + HqlEscapeUtil.escape(activity.getCollegeIds()) + "%");
			}
			// 专业
			if (null != activity.getMajorIds() && !StringUtils.isEmpty(activity.getMajorIds())) {
				hql.append(" and  a.majorIds like :majorIds ");
				values.put("majorIds","%" + HqlEscapeUtil.escape(activity.getMajorIds())+ "%");
			}
			// 班级
			if (activity.getClassIds() != null && !StringUtils.isEmpty(activity.getClassIds())) {
				hql.append(" and  a.classIds like :classIds ");
				values.put("classIds","%" + HqlEscapeUtil.escape(activity.getClassIds())+ "%");
			}
			if (!StringUtils.isEmpty(activity.getProcessStatus())) {
				if (activity.getProcessStatus().equals("PASS")) {
                    hql.append(" and  a.processStatus = (:processStatus) and a.suggest is not null ");
					values.put("processStatus",activity.getProcessStatus());
				}else{
					hql.append(" and  a.processStatus = (:processStatus) ");
					values.put("processStatus",activity.getProcessStatus());
				}
			}
		}
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);		
		}
	@Override
	public Page queryApproveActivityPage(int pageNo, int pageSize,ActivityBaseinfoModel activity, String userId,String[] objectIds, String[] objectIds1, String[] objectIds2, String[] objectIds3, String[] objectIds4) {

		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("select a from ActivityBaseinfoModel a where 1=1 and ( a.nextApprover.id = :userId or  a.id in (:objectIds) or a.id in (:objectIds1)or a.id in (:objectIds2)or a.id in (:objectIds3)or a.id in (:objectIds4)) ");
		values.put("userId",userId);
		values.put("objectIds",objectIds);
		values.put("objectIds1",objectIds1);
		values.put("objectIds2",objectIds2);
		values.put("objectIds3",objectIds3);
		values.put("objectIds4",objectIds4);
		if (null != activity) {
			//活动名称
			if(!StringUtils.isEmpty(activity.getActivityName())){
				hql.append(" and a.activityName like :activityName ");
				if (HqlEscapeUtil.IsNeedEscape(activity.getActivityName())){
					values.put("activityName","%" + HqlEscapeUtil.escape(activity.getActivityName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else
					values.put("activityName","%" +activity.getActivityName()+ "%");
			}
			//活动类型
			if(null!=activity.getActivityType() && !StringUtils.isEmpty(activity.getActivityType().getId())){
				hql.append(" and a.activityType.id = :activityTypeId ");
				values.put("activityTypeId",activity.getActivityType().getId());
			}
			//活动类别
			if(null!=activity.getActivityCategory() && !StringUtils.isEmpty(activity.getActivityCategory().getId())){
				hql.append(" and a.activityCategory.id = :activityCategoryId ");
				values.put("activityCategoryId",activity.getActivityCategory().getId());
			}
			//活动级别
			if(null!=activity.getActivityLevel() && !StringUtils.isEmpty(activity.getActivityLevel().getId())){
				hql.append(" and a.activityLevel.id = :activityLevelId ");
				values.put("activityLevelId",activity.getActivityLevel().getId());
			}
			//学院单位
			if (null != activity.getCollegeIds() && !StringUtils.isEmpty(activity.getCollegeIds())){
				
				hql.append(" and a.collegeIds like :collegeIds");
				values.put("collegeIds", "%" + HqlEscapeUtil.escape(activity.getCollegeIds()) + "%");
			}
			// 专业
			if (null != activity.getMajorIds() && !StringUtils.isEmpty(activity.getMajorIds())) {
				hql.append(" and  a.majorIds like :majorIds ");
				values.put("majorIds","%" + HqlEscapeUtil.escape(activity.getMajorIds())+ "%");
			}
			// 班级
			if (activity.getClassIds() != null && !StringUtils.isEmpty(activity.getClassIds())) {
				hql.append(" and  a.classIds like :classIds ");
				values.put("classIds","%" + HqlEscapeUtil.escape(activity.getClassIds())+ "%");
			}
			
			// 审核状态
			if (!StringUtils.isEmpty(activity.getProcessStatus())) {
				if(ProjectConstants.CURRENT_APPROVE_USER_PROCESS_CODE.equals(activity.getProcessStatus()))
				{
					hql.append(" and a.nextApprover.id = :approveUserId ");
					values.put("approveUserId",userId);
				}
				else
				{
					hql.append(" and a.processStatus = :processStatus ");
					values.put("processStatus",activity.getProcessStatus());
				}
			}
		}
		//排序
		hql.append(" order by a.processStatus desc");
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);
			
		}
	@Override
	public Page querySignUpActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize, String userId, String collegeId, String[] objectIds, String[] objectIds1, String volunteer, String leagueMember){
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("select a from ActivityBaseinfoModel a where 1=1 ");
		//活动报名时，以可报名状态为唯一报名条件，线下活动申请时将报名状态填为“否”  and a.creator.id <>:userId 
		if(!StringUtils.isEmpty(userId)){
			hql.append(" and a.isSignStatus.id=:isSignStatusId and a.id not in (:objectIds) and a.summaryStatus <>'SUBMIT'" );
			values.put("isSignStatusId",Constants.STATUS_YES.getId());
		    //values.put("userId",userId); 
			values.put("objectIds",objectIds);
		}
		hql.append(" and (a.activityLevel.id=:activityLevel " );
		//学校级活动
		values.put("activityLevel",dicUtil.getDicInfo("HKY_ACTIVITY_LEVEL", "SCHOOL_LEVEL").getId());
		//学院级活动
		if(!StringUtils.isEmpty(collegeId)){
			hql.append(" or a.activityLevel.id=:activityLevel1 and (a.activityCategory.id =:activityCategory and (select s.college.id from StudentInfoModel s where s.id= a.creator.id)=:collegeId or a.activityCategory.id =:activityCategory1 and a.collegeIds like:collegeId1)) ");
			values.put("activityLevel1",dicUtil.getDicInfo("HKY_ACTIVITY_LEVEL", "COLLEGE_LEVEL").getId());
			values.put("activityCategory",dicUtil.getDicInfo("HKY_ACTIVITY_CATEGORY", "ACTIVITY_INSIDE").getId());
			values.put("collegeId",collegeId);
			values.put("activityCategory1",dicUtil.getDicInfo("HKY_ACTIVITY_CATEGORY", "ACTIVITY_OUTSIDE").getId());
			values.put("collegeId1","%" + HqlEscapeUtil.escape(collegeId) + "%");
            //社团活动
			hql.append(" and ( a.activityType.id=:activityType2 and a.association.id in(:objectIds1)" );
			values.put("activityType2",dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_ASSOCIATION").getId());
			values.put("objectIds1",objectIds1);
			//是否为团员
			if(!StringUtils.isEmpty(leagueMember)){
				hql.append(" or a.activityType.id=:activityType" );
				values.put("activityType",dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_LEAGUE").getId());
			}
			//是否为志愿者
			if(!StringUtils.isEmpty(volunteer)){
				hql.append(" or a.activityType.id=:activityType1" );
				values.put("activityType1",dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_VOLUNTEER_SERVICE").getId());
			}
			
			//社会实践 和 其他活动
			hql.append(" or a.activityType.id=:activityType3 or a.activityType.id=:activityType4 )" );
			values.put("activityType3",dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_SOCIAL_PRACTICE").getId());
			values.put("activityType4",dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_OTHER").getId());
			
		}
		if (null != activity) {
			//活动名称
			if(!StringUtils.isEmpty(activity.getActivityName())){
				hql.append(" and a.activityName like :activityName ");
				if (HqlEscapeUtil.IsNeedEscape(activity.getActivityName())){
					values.put("activityName","%" + HqlEscapeUtil.escape(activity.getActivityName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else
					values.put("activityName","%" +activity.getActivityName()+ "%");
			}
			//活动类型
			if(null!=activity.getActivityType() && !StringUtils.isEmpty(activity.getActivityType().getId())){
				hql.append(" and a.activityType.id = :activityTypeId ");
				values.put("activityTypeId",activity.getActivityType().getId());
			}
			//活动类别
			if(null!=activity.getActivityCategory() && !StringUtils.isEmpty(activity.getActivityCategory().getId())){
				hql.append(" and a.activityCategory.id = :activityCategoryId ");
				values.put("activityCategoryId",activity.getActivityCategory().getId());
			}
			//活动级别
			if(null!=activity.getActivityLevel() && !StringUtils.isEmpty(activity.getActivityLevel().getId())){
				hql.append(" and a.activityLevel.id = :activityLevelId ");
				values.put("activityLevelId",activity.getActivityLevel().getId());
			}
			//学院单位
			if (null != activity.getCollegeIds() && !StringUtils.isEmpty(activity.getCollegeIds())){
				
				hql.append(" and a.collegeIds like :collegeIds");
				values.put("collegeIds", "%" + HqlEscapeUtil.escape(activity.getCollegeIds()) + "%");
				
			}
			
			// 专业
			if (null != activity.getMajorIds() && !StringUtils.isEmpty(activity.getMajorIds())) {
				hql.append(" and  a.majorIds like :majorIds ");
				values.put("majorIds","%" + HqlEscapeUtil.escape(activity.getMajorIds())+ "%");
			}
			// 班级
			if (activity.getClassIds() != null && !StringUtils.isEmpty(activity.getClassIds())) {
				hql.append(" and  a.classIds like :classIds ");
				values.put("classIds","%" + HqlEscapeUtil.escape(activity.getClassIds())+ "%");
			}
		}
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);		
		}
	
	@Override
	public Page querySelfActivityPage(ActivityMemberModel activityMember, int pageNo,int pageSize, String userId){
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("from ActivityMemberModel a where 1=1 "); 
        if(!StringUtils.isEmpty(userId)){
			hql.append(" and a.member.id = :memberId ");
			values.put("memberId", userId);
		}
//        设置活动审核状态为通过的
        hql.append(" and a.activityPo.processStatus = 'PASS'");
		if(null != activityMember){
			if(null != activityMember && null!=activityMember.getApproveStatus()){
		        if(!StringUtils.isEmpty(activityMember.getApproveStatus().getId())){
					hql.append(" and a.approveStatus.id = :approveStautsId ");
					values.put("approveStautsId", activityMember.getApproveStatus().getId());
				}
			}
			//查询条件是否存在
			if(null!=activityMember.getActivityPo()){
				//活动名称
				if(!StringUtils.isEmpty(activityMember.getActivityPo().getActivityName())){
					hql.append(" and a.activityPo.activityName like :activityName ");
					if (HqlEscapeUtil.IsNeedEscape(activityMember.getActivityPo().getActivityName())){
						
						values.put("activityName","%" + HqlEscapeUtil.escape(activityMember.getActivityPo().getActivityName()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					}else
						values.put("activityName","%" +activityMember.getActivityPo().getActivityName()+ "%");
				}
				//活动类型
				if(null!=activityMember.getActivityPo().getActivityType() && !StringUtils.isEmpty(activityMember.getActivityPo().getActivityType().getId())){
					hql.append(" and a.activityPo.activityType.id = :activityTypeId ");
					values.put("activityTypeId",activityMember.getActivityPo().getActivityType().getId());
				}
				//活动类别
				if(null!=activityMember.getActivityPo().getActivityCategory() && !StringUtils.isEmpty(activityMember.getActivityPo().getActivityCategory().getId())){
					hql.append(" and a.activityPo.activityCategory.id = :activityCategoryId ");
					values.put("activityCategoryId",activityMember.getActivityPo().getActivityCategory().getId());
				}
				//活动级别
				if(null!=activityMember.getActivityPo().getActivityLevel() && !StringUtils.isEmpty(activityMember.getActivityPo().getActivityLevel().getId())){
					hql.append(" and a.activityPo.activityLevel.id = :activityLevelId");
					values.put("activityLevelId",activityMember.getActivityPo().getActivityLevel().getId());
				}
				//学院单位
				if (null != activityMember.getActivityPo().getCollegeIds() && !StringUtils.isEmpty(activityMember.getActivityPo().getCollegeIds())){
					hql.append(" and a.activityPo.collegeIds like :collegeIds ");
					values.put("collegeIds","%" + HqlEscapeUtil.escape(activityMember.getActivityPo().getCollegeIds())+ "%");
				}
				// 专业
				if (null != activityMember.getActivityPo().getMajorIds() && !StringUtils.isEmpty(activityMember.getActivityPo().getMajorIds())) {
					hql.append(" and a.activityPo.majorIds like :majorIds ");
					values.put("majorIds","%" + HqlEscapeUtil.escape(activityMember.getActivityPo().getMajorIds())+ "%");
				}
				// 班级
				if (activityMember.getActivityPo().getClassIds() != null && !StringUtils.isEmpty(activityMember.getActivityPo().getClassIds())) {
					hql.append(" and a.activityPo.classIds like :classIds ");
					values.put("classIds","%" + HqlEscapeUtil.escape(activityMember.getActivityPo().getClassIds())+ "%");
				}
				
			}
		}
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values, pageSize, pageNo);		
		
	}
	/** 
	* @Title: deleteActivity 
	* @Description: 删除活动信息（修改活动删除状态）
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivity(String objectId) {
		if(!StringUtils.isEmpty(objectId)){
			List<Object> list =new ArrayList<Object>();
			String hql="update ActivityBaseinfoModel a set a.deleteStatus.id=? where a.id=?";
			//删除状态
			list.add(Constants.STATUS_DELETED.getId());
			list.add(objectId);
			this.executeHql(hql, list.toArray());
			}	
	}
	/** 
	* @Title: updateApproveActivity 
	* @Description: 修改活动信息的审核信息
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void updateApproveActivity(ActivityBaseinfoModel activity){
		//最后一级审核通过后	将活动报名状态修改为可报名
		if(activity!=null && activity.getProcessStatus()!=null && activity.getProcessStatus().equals("PASS") && activity.getRegistraForm().getId().equals(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_ONLINE").getId())){
			//可报名
			activity.setIsSignStatus(Constants.STATUS_YES);
		}else{
			//不报名
			activity.setIsSignStatus(Constants.STATUS_NO);
		}
		if(activity!=null){
			List<Object> list =new ArrayList<Object>();
			String hql="update ActivityBaseinfoModel a set a.isSignStatus.id = ?, a.suggest = ?, a.processStatus = ?, a.approveResult = ?, a.nextApprover.id = ? where a.id=?";
			list.add(activity.getIsSignStatus().getId());
			list.add(activity.getSuggest());
			list.add(activity.getProcessStatus());
			list.add(activity.getApproveResult());
			list.add(activity.getNextApprover()!=null?activity.getNextApprover().getId():null);
			list.add(activity.getId());
			this.executeHql(hql, list.toArray());	
		}
	}
	/** 
	* @Title: updateActivitySignStatus 
	* @Description: 修改活动报名状态
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void updateActivitySignStatus(String objectId,String isSignStatus) {
		if(!StringUtils.isEmpty(objectId)){
			List<Object> list =new ArrayList<Object>();
			String hql="update ActivityBaseinfoModel a set a.isSignStatus.id=? where a.id=?";
			//删除状态
			list.add(isSignStatus);
			list.add(objectId);
			this.executeHql(hql, list.toArray());	
			}	
	}
	/** 
	* @Title: addActivitySummary 
	* @Description: 添加活动总结
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void addActivitySummary(ActivityBaseinfoModel activity) {
		if(!StringUtils.isEmpty(activity.getId())){
			List<Object> list =new ArrayList<Object>();
			String hql="update ActivityBaseinfoModel a set a.summaryStatus=? ,a.summary=? ,a.holdStatus.id= ?, a.reason=? where a.id=?";
			//活动总结状态
			list.add(activity.getSummaryStatus());
			//活动总结
			list.add(activity.getSummary());
			//活动举办状态
			list.add((activity.getHoldStatus()!=null)?activity.getHoldStatus().getId():"");
			//活动未举办原因
			list.add(activity.getReason());
			list.add(activity.getId());
			this.executeHql(hql, list.toArray());	
			}	
	}
	/** 
	* @Title: updateActivityStatus 
	* @Description: 修改活动状态
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void updateActivityStatus(String objectId, String status) {
		if(!StringUtils.isEmpty(objectId)){
			List<Object> list =new ArrayList<Object>();
			String hql="update ActivityBaseinfoModel a set  a.applyTime=?, a.status=? where a.id=?";
			/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date=null;
			try {
				date=df.parse(df.format(new Date()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			list.add(new Date());
			list.add(status);
			list.add(objectId);
			this.executeHql(hql, list.toArray());	
			}	
		}
	/** 
	* @Title: queryActivityByActivityName 
	* @Description: 活动名称的惟一性验证
	* @param  @param ActivityBaseinfoModel  
	* @throws 
	*/
	@Override
	public ActivityBaseinfoModel queryActivityByActivityName(String activityName, String activityId){
		return (ActivityBaseinfoModel)this.queryUnique(" from ActivityBaseinfoModel a where 1=1 and a.activityName = ? and a.id != ?",activityName,!StringUtils.isEmpty(activityId)?activityId:"1");

	}
	
	/**----- 活动参与人员维护-------*/

	/** 
	* @Title: queryActivityMemberPage 
	* @Description: 查询活动参与人员列表
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public Page queryActivityMemberPage(int pageNo, int pageSize, ActivityMemberModel activityMember, String activityId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ActivityMemberModel a where 1=1 ");
		if(!StringUtils.isEmpty(activityId)){
			hql.append(" and a.activityPo.id = ? and a.deleteStatus.id = ? ");
			values.add(activityId);
			values.add(Constants.STATUS_NORMAL.getId());
		}
		if (null != activityMember) {
			if (null != activityMember.getMember()) {
				//姓名
				if(null!=activityMember.getMember()&& !StringUtils.isEmpty(activityMember.getMember().getName())){
					hql.append(" and a.member.name like ? ");
					if (HqlEscapeUtil.IsNeedEscape(activityMember.getMember().getName())){
						values.add("%" + HqlEscapeUtil.escape(activityMember.getMember().getName()) + "%");
						hql.append(HqlEscapeUtil.HQL_ESCAPE);
					}else
						values.add("%" +activityMember.getMember().getName()+ "%");
				}
				//学院
				if (null != activityMember.getMember().getCollege() && !StringUtils.isEmpty(activityMember.getMember().getCollege().getId())){
					hql.append(" and a.member.college.id = ? ");
					values.add(activityMember.getMember().getCollege().getId());
				}
				// 专业
				if (null != activityMember.getMember().getMajor() && !StringUtils.isEmpty(activityMember.getMember().getMajor().getId())) {
					hql.append(" and  a.member.major.id = ? ");
					values.add(activityMember.getMember().getMajor().getId());
				}
				// 班级
				if (activityMember.getMember().getClassId() != null && !StringUtils.isEmpty(activityMember.getMember().getClassId().getId())) {
					hql.append(" and  a.member.classId.id = ? ");
					values.add(activityMember.getMember().getClassId().getId());
				}
			}
			//活动类型
			if(null!=activityMember.getApproveStatus() && !StringUtils.isEmpty(activityMember.getApproveStatus().getId())){
				hql.append(" and a.approveStatus.id = ? ");
				values.add(activityMember.getApproveStatus().getId());
			}
		}
        if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
	}
	/** 
	* @Title: deleteActivityMember 
	* @Description: 删除活动参与人员
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivityMember(String activityId, String studentId) {
		this.executeHql(" delete from ActivityMemberModel a where a.activityPo.id=? and a.member.id=? and a.registraForm.id=? ", activityId, studentId, dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE").getId());
		//this.executeHql(" update ActivityMemberModel a set a.deleteStatus.id = ? where a.activityPo.id=? and a.member.id=?",Constants.STATUS_DELETED.getId(),activityId,studentId);
	}
	/** 
	* @Title: updateActivityMember 
	* @Description:修改活动参与人员的审核状态
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void updateActivityMember(String activityId, String memberId, String approveStatus, String suggest){
		List<Object> list =new ArrayList<Object>();
		String hql="update ActivityMemberModel a set a.approveStatus.id=? , a.suggest=? where a.activityPo.id=? and a.member.id=?";
		list.add(approveStatus);
		list.add(suggest);
		list.add(activityId);
		list.add(memberId);
		this.executeHql(hql, list.toArray());
	}
	/** 
	* @Title: getActivityMember 
	* @Description: 根据活动id学生id查询学生是否参加了该活动
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public ActivityMemberModel getActivityMember(String activityId, String studentId) {
		return (ActivityMemberModel)this.queryUnique(" from ActivityMemberModel a where a.activityPo.id=? and a.member.id=? and a.deleteStatus.id =?",activityId,studentId,Constants.STATUS_NORMAL.getId());
	}
	/** 
	* @Title: queryActivityMemberList 
	* @Description: 活动参与人员信息列表
	* @param  @param ActivityMemberModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryActivityMemberList(String activityId){
		List<ActivityMemberModel> list = this.query("from ActivityMemberModel a where a.activityPo.id=?",activityId);
		return list;
	}
	/** 
	* @Title: queryOffActivityMemberList 
	* @Description: 活动参与人员信息列表(线下报名)
	* @param  @param ActivityMemberModel    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryOffActivityMemberList(String activityId){
		List<ActivityMemberModel> list = this.query("from ActivityMemberModel a where a.activityPo.id=? and a.registraForm.id=?",activityId, dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE").getId());
		return list;
	}
	/** 
	* @Title: queryActivityPassMemberList 
	* @Description: 活动已通过参与人员信息列表
	* @param  @param ActivityMemberModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryActivityPassMemberList(String activityId){
		List<ActivityMemberModel> list = this.query("from ActivityMemberModel a where a.activityPo.id=?  and a.approveStatus=?",activityId,dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
		return list;
	}
	/** 
	* @Title: deleteMemberByActivityId 
	* @Description: 根据活动id删除所用参与人员(线下报名)
	* @param  @param ActivityMemberModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	/*public void deleteMemberByActivityId(String activityId){
		this.executeHql("delete from ActivityMemberModel a where a.activityPo.id = ? ", activityId);
	}*/
	public void deleteMemberByActivityId(String activityId){
		this.executeHql("delete from ActivityMemberModel a where a.activityPo.id = ? and a.registraForm.id = ?", activityId, dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE").getId());
	}
	/** 
	* @Title: queryActivityByMemberIdList 
	* @Description: 查询当前登录人已报名或已申请的活动信息列表
	* @param  @param ActivityBaseinfoModel  
	* @throws 
	*/
	@Override
	public List<ActivityBaseinfoModel> queryActivityByMemberIdList(String userId){
		List<ActivityBaseinfoModel> list = this.query("select m.activityPo from ActivityMemberModel m where m.member.id = ? and m.deleteStatus.id = ?",userId,Constants.STATUS_NORMAL.getId());
		return list;
	}
	/**----- 活动工作人员维护-------*/
	/** 
	* @Title: deleteActivityWorker 
	* @Description: 根据活动id删除所有活动工作人员
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivityWorker(String activityId) {
		StringBuffer hql = new StringBuffer(" delete from ActivityWorkerModel a where a.activityPo.id=?");
		this.executeHql(hql.toString(), activityId);
	}
	/** 
	* @Title: queryActivityWorkersPage 
	* @Description: 根据活动id查询活动工作人员page
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public Page queryActivityWorkersPage(int pageNo,int pageSize,String activityId) {
        List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer("from ActivityWorkerModel a where a.activityPo.id=?");
		values.add(activityId);
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
	}
	/** 
	* @Title: queryActivityWorkerList 
	* @Description: 根据活动id查询活动工作人员列表
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public List<ActivityWorkerModel> queryActivityWorkerList(String activityId) {
		List<ActivityWorkerModel> list = this.query("from ActivityWorkerModel a where a.activityPo.id=?",activityId);
		return list;
	}
	
	/**----- 活动带队老师维护-------*/
	
	/** 
	* @Title: deleteActivityTeacher 
	* @Description: 根据活动id删除所有活动带队老师
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivityTeacher(String activityId) {
		StringBuffer hql = new StringBuffer(" delete from ActivityTeacherModel a where a.activityPo.id=?");
		this.executeHql(hql.toString(), activityId);
	}
	/** 
	* @Title: queryActivityTeacherList 
	* @Description: 活动带队老师信息列表
	* @param  @param ActivityWorkerModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public List<ActivityTeacherModel> queryActivityTeacherList(String activityId){
		List<ActivityTeacherModel> list = this.query("from ActivityTeacherModel a where a.activityPo.id=?",activityId);
		return list;
	}
	/** 
	* @Title: queryActivityTeacherPage 
	* @Description: 根据活动id查询活动工作人员page
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public Page queryActivityTeacherPage(int pageNo,int pageSize,String activityId){
        List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from ActivityTeacherModel a where a.activityPo.id=?");
		values.add(activityId);
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
     }
	
	/** 
	* @Title: queryStatisticActivityPage 
	* @Description: 活动管理监管分析查询列表
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public Page queryStatisticActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize) {
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer hql = new StringBuffer("from ActivityBaseinfoModel a where 1=1 ");
		//查询条件是否存在
		if(null != activity){
			//活动名称
			if(!StringUtils.isEmpty(activity.getActivityName())){
				hql.append(" and a.activityName like  :activityName");
				if (HqlEscapeUtil.IsNeedEscape(activity.getActivityName())){
					values.put("activityName","%" + HqlEscapeUtil.escape(activity.getActivityName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else
					values.put("activityName","%" +activity.getActivityName()+ "%");
			}
			//活动类型
			if(null!=activity.getActivityType() && !StringUtils.isEmpty(activity.getActivityType().getId())){
				hql.append(" and a.activityType.id = :activityTypeId ");
				values.put("activityTypeId",activity.getActivityType().getId());
			}
			//活动级别
			if(null!=activity.getActivityLevel() && !StringUtils.isEmpty(activity.getActivityLevel().getId())){
				hql.append(" and a.activityLevel.id = :activityLevelId ");
				values.put("activityLevelId",activity.getActivityLevel().getId());
			}
			//活动类别
			if(null!=activity.getActivityCategory() && !StringUtils.isEmpty(activity.getActivityCategory().getId())){
				hql.append(" and a.activityCategory.id = :activityCategoryId");
				values.put("activityCategoryId",activity.getActivityCategory().getId());
			}
			//学院单位
			if (null != activity.getCollegeIds() && !StringUtils.isEmpty(activity.getCollegeIds())){
				hql.append(" and a.collegeIds like:collegeIds");
				values.put("collegeIds","%" + HqlEscapeUtil.escape(activity.getCollegeIds()) + "%");
			}
			// 专业
			if (null != activity.getMajorIds() && !StringUtils.isEmpty(activity.getMajorIds())) {
				hql.append(" and  a.majorIds like :majorIds ");
				values.put("majorIds","%" + HqlEscapeUtil.escape(activity.getMajorIds()) + "%");
			}
			// 班级
			if (activity.getClassIds() != null && !StringUtils.isEmpty(activity.getClassIds())) {
				hql.append(" and  a.classIds like :classIds ");
				values.put("classIds","%" + HqlEscapeUtil.escape(activity.getClassIds()) + "%");
			}
			}
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), values,pageNo, pageSize);	
		
		}
	
}