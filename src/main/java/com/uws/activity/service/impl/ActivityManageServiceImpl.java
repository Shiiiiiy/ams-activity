package com.uws.activity.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.activity.dao.IActivityManageDao;
import com.uws.activity.service.IActivityManageService;
import com.uws.apw.model.ApproveResult;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.Constants;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.activity.ActivityBaseinfoModel;
import com.uws.domain.activity.ActivityMemberModel;
import com.uws.domain.activity.ActivityTeacherModel;
import com.uws.domain.activity.ActivityWorkerModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;

@Service("com.uws.activity.service.impl.ActivityManageServiceImpl")
public class ActivityManageServiceImpl extends BaseServiceImpl implements IActivityManageService {
	@Autowired
	private IActivityManageDao activityManageDao;
	@Autowired
	private IFlowInstanceService flowInstanceService;
	// 基础数据service
	@Autowired
	private IBaseDataService baseDataService;
		
	//数据字典
    private DicUtil dicUtil = DicFactory.getDicUtil();
    // fileUtil工具类
 	private FileUtil fileUtil=FileFactory.getFileUtil();
	/** 
	* @Title: queryApproveActivityPage 
	* @Description: 维护信息列表
	* @param  @param activity    
	* @return Page    
	* @throws 
	*/
	public Page queryManageActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize, String userId){
		return activityManageDao.queryManageActivityPage(activity, pageNo, pageSize, userId);
	}
 	/** 
	* @Title: queryApproveActivityPage 
	* @Description: 审核信息列表
	* @param  @param activity    
	* @return Page    
	* @throws 
	*/
	@Override
	public Page queryApproveActivityPage(int pageNo, int pageSize,ActivityBaseinfoModel activity, String userId,String[] objectIds, String[] objectIds1, String[] objectIds2, String[] objectIds3, String[] objectIds4){
		return activityManageDao.queryApproveActivityPage( pageNo, pageSize, activity, userId, objectIds, objectIds1,objectIds2,objectIds3,objectIds4);
	}
	/** 
	* @Title: querySignUpActivityPage 
	* @Description: 报名信息列表（活动维护信息列表）
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public Page querySignUpActivityPage(ActivityBaseinfoModel activity, int pageNo, int pageSize, String userId, String collegeId, String[] objectIds, String[] objectIds1, String volunteer, String leagueMember) {
		return activityManageDao.querySignUpActivityPage(activity, pageNo, pageSize,userId,collegeId,objectIds,objectIds1,volunteer,leagueMember);
	}
	/** 
	* @Title: querySelfActivityPage 
	* @Description: 我的活动信息列表
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public Page querySelfActivityPage(ActivityMemberModel activityMember, int pageNo,int pageSize, String userId){
		return this.activityManageDao.querySelfActivityPage(activityMember, pageNo, pageSize, userId);
	}
	/** 
	* @Title: saveActivity 
	* @Description: 保存活动信息，要先删除活动工作人员信息，再添加工作人员信息
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void saveActivity(ActivityBaseinfoModel activity,String workerIds,String teacherIds) {
		activityManageDao.save(activity);
		//删除表中的工作人员数据
		activityManageDao.deleteActivityWorker(activity.getId());
		//删除表中的带队老师人员数据
		activityManageDao.deleteActivityTeacher(activity.getId());
		//添加工作人员数据
		//String workerIdArray [] = workerIds.split(",");
		//for(int i=0;i<workerIdArray.length;i++){
			//String workerId = workerIdArray[i];
			//判断工作人员id在用户表中是否存在
			//BaseTeacherModel user = (BaseTeacherModel) this.activityManageDao.get(BaseTeacherModel.class, workerId);
			//if(DataUtil.isNotNull(user)){
				ActivityWorkerModel activityWorkerModel=new ActivityWorkerModel();
				activityWorkerModel.setActivityPo(activity);
			//	activityWorkerModel.setWorkerPo(user);
				activityManageDao.save(activityWorkerModel);
			//}
		//}	
		//添加带队老师人员数据
		String teacherIdArray [] = teacherIds.split(",");
		for(int i=0;i<teacherIdArray.length;i++){
			String teacherId = teacherIdArray[i];
			//判断教工id在用户表中是否存在
			BaseTeacherModel leaderTeacher = (BaseTeacherModel) activityManageDao.get(BaseTeacherModel.class, teacherId);
			if(DataUtil.isNotNull(leaderTeacher)){
				ActivityTeacherModel activityTeacherModel=new ActivityTeacherModel();
				activityTeacherModel.setActivityPo(activity);
				activityTeacherModel.setLeaderTeacher(leaderTeacher);
				activityManageDao.save(activityTeacherModel);
			}
		}
	}
	/** 
	* @Title: updateActivity 
	* @Description: 修改活动信息（包括活动工作人员，带队教师）
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateActivity(ActivityBaseinfoModel activity,String workerIds,String teacherIds) {
		//活动保存时要提交活动基本信息，活动工作人员，活动带队老师。要先对活动工作人员，活动带队老师表中信息进行删除，在对工作人员，带队教师信息进行添加
		activityManageDao.update(activity);
		//删除表中的工作人员数据
		activityManageDao.deleteActivityWorker(activity.getId());
		//删除表中的带队老师人员数据
		activityManageDao.deleteActivityTeacher(activity.getId());
		//添加工作人员数据
		
		//String workerIdArray [] = workerIds.split(",");
		//for(int i=0;i<workerIdArray.length;i++){
		//	String workerId = workerIdArray[i];
			//判断工作人员id在用户表中是否存在
		//	BaseTeacherModel user = (BaseTeacherModel) this.activityManageDao.get(BaseTeacherModel.class, workerId);
		//	if(DataUtil.isNotNull(user)){
				ActivityWorkerModel activityWorkerModel=new ActivityWorkerModel();
				activityWorkerModel.setActivityPo(activity);
			//	activityWorkerModel.setWorkerPo(user);
				activityManageDao.save(activityWorkerModel);
		//	}
		//}	
		//添加带队老师人员数据
		String teacherIdArray [] = teacherIds.split(",");
		for(int i=0;i<teacherIdArray.length;i++){
			String teacherId = teacherIdArray[i];
			//判断教工id在用户表中是否存在
			BaseTeacherModel leaderTeacher = (BaseTeacherModel) activityManageDao.get(BaseTeacherModel.class, teacherId);
			if(DataUtil.isNotNull(leaderTeacher)){
				ActivityTeacherModel activityTeacherModel=new ActivityTeacherModel();
				activityTeacherModel.setActivityPo(activity);
				activityTeacherModel.setLeaderTeacher(leaderTeacher);
				activityManageDao.save(activityTeacherModel);
			}
		}
	}
	/** 
	* @Title: updateActivityInfo 
	* @Description: 修改活动基本信息
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateActivityInfo(ActivityBaseinfoModel activity){
		activityManageDao.update(activity);
	}
	/** 
	* @Title: updateApproveActivity 
	* @Description: 修改审核信息
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateApproveActivity(ActivityBaseinfoModel activity){
		activityManageDao.updateApproveActivity(activity);
	}
	/** 
	* @Title: deleteActivity 
	* @Description: 删除
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void deleteActivity(String id) {
		activityManageDao.deleteById(ActivityBaseinfoModel.class,id);
	}
	/** 
	* @Title: queryActivity 
	* @Description: 查询（根据主键id）
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public ActivityBaseinfoModel queryActivity(String id) {
		return 	(ActivityBaseinfoModel) activityManageDao.get(ActivityBaseinfoModel.class, id);
	}	
	/**
	 * @Title: saveActivityApproveResult
	 * @Description: 保存活动信息审批结果
	 * @param objectId
	 * @param result
	 * @throws
	 */
	@Override
	public ApproveResult saveActivityApproveResult(String objectId, ApproveResult result, String nextApproverId, String approveReason) {
		if(DataUtil.isNotNull(result)){ 
			//获取保存的活动信息
			ActivityBaseinfoModel activity  = (ActivityBaseinfoModel) this.activityManageDao.get(ActivityBaseinfoModel.class, objectId);
			if(DataUtil.isNotNull(result.getProcessStatusCode()) && result.getProcessStatusCode().equals("REJECT")) {
				activity.setStatus("SAVE");
			}
			activity.setProcessStatus(result.getApproveResultCode());
			//节点审批结果
			activity.setApproveResult(result.getApproveStatus());
			activity.setSuggest(approveReason);
			if(DataUtil.isNotNull(nextApproverId)){
				//下一节点办理人
				User nextApprover = new User();
				nextApprover.setId(nextApproverId);
				activity.setNextApprover(nextApprover);
			}else{
				activity.setNextApprover(null);
			}
			//保存审批流回显的信息
			activityManageDao.updateApproveActivity(activity);
		}
		
		return result;
	}
	/**
	 * @Title: submitApprove
	 * @Description: 审批 修改状态 添加下一节点人
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws
	 */
	public ApproveResult submitApprove(String objectId,User initiator,User nextApprover){
		
        ApproveResult result = new ApproveResult();
        ActivityBaseinfoModel activity =(ActivityBaseinfoModel) this.activityManageDao.get(ActivityBaseinfoModel.class, objectId);
        int members=activity.getMembers();
        //发起校内活动
        if(activity.getActivityCategory()!=null && activity.getActivityCategory().getId().equals(dicUtil.getDicInfo("HKY_ACTIVITY_CATEGORY", "ACTIVITY_INSIDE").getId())&&members>=200){
    		result = flowInstanceService.initProcessInstance(objectId,"ACTIVITY_INFO_INSIDE_APPROVE", 
    				 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
			result = this.saveActivityApproveResult(objectId, result, nextApprover.getId(),null);
    		this.activityManageDao.updateActivityStatus(objectId,Constants.OPERATE_STATUS.SUBMIT.toString());
    		result.setResultFlag("success");
        }else if(activity.getActivityCategory()!=null && activity.getActivityCategory().getId().equals(dicUtil.getDicInfo("HKY_ACTIVITY_CATEGORY", "ACTIVITY_OUTSIDE").getId())&&members>=30){
        	//发起校外活动
        	if(activity.getActivityLevel()!=null && (dicUtil.getDicInfo("HKY_ACTIVITY_LEVEL", "SCHOOL_LEVEL").getId()).equals(activity.getActivityLevel().getId())){
        		//学校级
        		result = flowInstanceService.initProcessInstance(objectId,"ACTIVITY_INFO_OUTSIDE_SCHOOL_APPROVE", 
       				 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
        	}else{
        		//学院级
        		result = flowInstanceService.initProcessInstance(objectId,"ACTIVITY_INFO_OUTSIDE_COLLEGE_APPROVE", 
       				 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
        	}
			result = this.saveActivityApproveResult(objectId, result, nextApprover.getId(),null);    		
    		this.activityManageDao.updateActivityStatus(objectId,Constants.OPERATE_STATUS.SUBMIT.toString());
    		result.setResultFlag("success");
        }else if(activity.getActivityLevel()!=null && (dicUtil.getDicInfo("HKY_ACTIVITY_LEVEL", "SCHOOL_LEVEL").getId()).equals(activity.getActivityLevel().getId())){
        	//zhangmx 1.判断校级与院级 走不同的审批
        	//学校级
        	result = flowInstanceService.initProcessInstance(objectId,"ACTIVITY_INFO_SCHOOL_APPROVE", 
   				 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
			result = this.saveActivityApproveResult(objectId, result, nextApprover.getId(),null);    		
	   		this.activityManageDao.updateActivityStatus(objectId,Constants.OPERATE_STATUS.SUBMIT.toString());
	   		result.setResultFlag("success");
    		
        }else if(activity.getActivityLevel()!=null && (dicUtil.getDicInfo("HKY_ACTIVITY_LEVEL", "COLLEGE_LEVEL").getId()).equals(activity.getActivityLevel().getId())){
        	//学院级ACTIVITY_INFO_COLLEGE_APPROVE
        	result = flowInstanceService.initProcessInstance(objectId,"ACTIVITY_INFO_COLLEGE_APPROVE", 
      				 initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
   			result = this.saveActivityApproveResult(objectId, result, nextApprover.getId(),null);    		
   	   		this.activityManageDao.updateActivityStatus(objectId,Constants.OPERATE_STATUS.SUBMIT.toString());
   	   		result.setResultFlag("success");
        }else{
        	result.setResultFlag("error");
        }
		return result;
		
	}
	/** 
	* @Title: updateActivitySignStatus 
	* @Description: 修改报名状态
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateActivitySignStatus(String objectId, String isSignStatus) {
		this.activityManageDao.updateActivitySignStatus(objectId, isSignStatus);
	}
	/** 
	* @Title: updateActivitySummary 
	* @Description: 添加活动总结
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateActivitySummary(ActivityBaseinfoModel activity, String[] fileId) {
		
		this.activityManageDao.addActivitySummary(activity);	
		//上传的附件进行处理       新增
		 /*if (ArrayUtils.isEmpty(fileId)) {
		       return;
		    }
		 for (String id : fileId){
			 this.fileUtil.updateFormalFileTempTag(id, honor.getId());
		 }*/
		//附件上传    修改
		if (ArrayUtils.isEmpty(fileId))
		       fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(activity.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
		         this.fileUtil.deleteFormalFile(ufr);
		     }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, activity.getId());
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
		return activityManageDao.queryActivityByActivityName(activityName,activityId);
	}
	/** 
	* @Title: queryActivityPage 
	* @Description: 活动管理监管分析查询列表
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public Page queryStatisticActivityPage(ActivityBaseinfoModel activity, int pageNo,int pageSize){
		return this.activityManageDao.queryStatisticActivityPage(activity, pageNo, pageSize);
	}
	/*----------参与人员维护------------*/
	/** 
	* @Title: getActivityMember
	* @Description: 查询学生是否参加该活动
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override   
	public ActivityMemberModel getActivityMember(String activityId, String studentId) {
		return (ActivityMemberModel) this.activityManageDao.getActivityMember(activityId, studentId);
	}
	/** 
	* @Title: saveActivityMember
	* @Description: 保存活动参与人员
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override   
	public String saveActivityMember(ActivityMemberModel activityMemberModel) {
		ActivityBaseinfoModel activity =(ActivityBaseinfoModel)this.activityManageDao.get(ActivityBaseinfoModel.class,activityMemberModel.getActivityPo().getId());
		List<ActivityMemberModel> activityMember=queryActivityPassMemberList(activityMemberModel.getActivityPo().getId());
        if(activityMemberModel.getApproveStatus()!=dicUtil.getDicInfo("APPLY_APPROVE", "PASS")||activityMember.size()<=activity.getMembers()){
			this.activityManageDao.save(activityMemberModel);
			return "success";
		}
			return "活动参与总人数为"+activity.getMembers()+"，还可以添加"+(activity.getMembers()-activityMember.size())+"人";
	}
	/** 
	* @Title: saveActivityMembers 
	* @Description: 保存活动参与人员（多个）
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	/*public void saveActivityMembers(String activityId, String studentIds) {
		ActivityBaseinfoModel activity=(ActivityBaseinfoModel) this.activityManageDao.get(ActivityBaseinfoModel.class, activityId);
		
		if(activity!=null){
			activityManageDao.deleteMemberByActivityId(activityId);
			String memberIds [] = studentIds.split(",");
			for(int i=0;i<memberIds.length;i++){
				String memberId = memberIds[i];
				ActivityMemberModel activityMember=activityManageDao.getActivityMember(activityId, memberId);
				if(activityMember!=null){
					this.activityManageDao.updateActivityMember(activityId, memberId, dicUtil.getDicInfo("APPLY_APPROVE", "PASS").getId(), null);
				}else{
					ActivityMemberModel activityMemberModel=new ActivityMemberModel();
					activityMemberModel.setActivityPo(activity);
					activityMemberModel.setApproveStatus(dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
					activityMemberModel.setDeleteStatus(dicUtil.getDicInfo("STATUS_NORMAL_DELETED", "NORMAL"));
					StudentInfoModel member= (StudentInfoModel) activityManageDao.get(StudentInfoModel.class, memberId);
					if(member!=null){
						activityMemberModel.setMember(member);
						this.activityManageDao.save(activityMemberModel);
					}
				}
				
			}
		}
	}*/
	public void saveActivityMembers(String activityId, String studentIds) {
		ActivityBaseinfoModel activity=(ActivityBaseinfoModel) this.activityManageDao.get(ActivityBaseinfoModel.class, activityId);
		//String rejectMembers=null;
		if(activity!=null){
			//删除所有线下报名的参与人员
			activityManageDao.deleteMemberByActivityId(activityId);
			String memberIds [] = studentIds.split(",");
			for(int i=0;i<memberIds.length;i++){
				String memberId = memberIds[i];
				StudentInfoModel member= (StudentInfoModel) activityManageDao.get(StudentInfoModel.class, memberId);
				if(member!=null){
					ActivityMemberModel activityMember=this.activityManageDao.getActivityMember(activityId, memberId);
					if(activityMember!=null){
						this.activityManageDao.updateActivityMember(activityId, memberId,dicUtil.getDicInfo("APPLY_APPROVE", "PASS").getId(), null);
					}else{
						ActivityMemberModel activityMemberModel=new ActivityMemberModel();
						activityMemberModel.setRegistraForm(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE"));
						activityMemberModel.setActivityPo(activity);
						activityMemberModel.setApproveStatus(dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
						activityMemberModel.setDeleteStatus(Constants.STATUS_NORMAL);
						activityMemberModel.setMember(member);
				    	this.activityManageDao.save(activityMemberModel);
					}
				}
			}
		}
	}
	/** 
	* @Title: deleteActivityAllMember 
	* @Description: 删除所用活动参与人员
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void deleteActivityAllMember(String activityId) {
		this.activityManageDao.deleteMemberByActivityId(activityId);
	}
	/** 
	* @Title: queryActivityMemberPage 
	* @Description: 活动参与人员列表
	* @param  @param ActivityBaseinfoModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return Page    
	* @throws 
	*/
	@Override
	public Page queryActivityMemberPage(int pageNo, int pageSize, ActivityMemberModel activityMember, String activityId) {
		return activityManageDao.queryActivityMemberPage(pageNo,pageSize,activityMember,activityId);
	}
	/** 
	* @Title: deleteActivityMembers 
	* @Description: 删除活动参与人员
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void deleteActivityMembers(String activityId, String ids) {
		//要删除的参与人员id
		String memberIds [] = ids.split(",");
		for(int i=0;i<memberIds.length;i++){
			String memberId = memberIds[i];
			//删除活动id为activityId的参与人员id为memberId的信息
			this.activityManageDao.deleteActivityMember(activityId,memberId);
		}	
	}
	/** 
	* @Title: updateActivityMembers 
	* @Description: 修改活动参与人员的审核状态
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public String updateActivityMembers(String activityId, String ids, String approveStatus, String suggest) {
		ActivityBaseinfoModel activity =(ActivityBaseinfoModel)this.activityManageDao.get(ActivityBaseinfoModel.class,activityId);
		List<ActivityMemberModel> activityMember=queryActivityPassMemberList(activityId);
		//审核通过的参与人员id
		String memberIds [] = ids.split(",");
		if(approveStatus==dicUtil.getDicInfo("APPLY_APPROVE","REJECT").getId()|| activity.getMembers()>=activityMember.size()+memberIds.length){
			for(int i=0;i<memberIds.length;i++){
				String memberId = memberIds[i];
				//删除活动id为activityId的参与人员id为memberId的信息
				this.activityManageDao.updateActivityMember(activityId,memberId,approveStatus,suggest);
			}
			return "";
		}
			return "批量审核失败，若通过人数将会超过总人数"+activity.getMembers()+"，还可以在添加"+(activity.getMembers()-activityMember.size())+"人！";
	}
	/** 
	 * @Title: importActivityMember
	 * @Description: 导入活动参与学生列表
	 * @return
	 * @throws
	 */
	@Override
	public String importActivityMember(String filePath, String importId, Map initDate, Class c, String activityId) throws Exception{
				ImportUtil iu = new ImportUtil();
				// 将Excel数据映射成对象List
				List<ActivityMemberModel> list = iu.getDataList(filePath, importId, initDate, c);
				// 错误信息
				String message = "";
				if (list != null && list.size() > 0){
						// 把导入的数据保存到数据库中
						String stuNumber = "";
						for (ActivityMemberModel activityMember : list) { 
							//取出学号
							stuNumber = activityMember.getStuNumber();
							if (!StringUtils.isEmpty(stuNumber)){
								IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
								//通过身份证号查询学生信息是否存在
								StudentInfoModel studentInfo=studentCommonService.queryStudentByStudentNo(stuNumber);
								if(studentInfo!=null){
									//通过学号查询该学生是否已参加该活动
									ActivityMemberModel activityMemberModel=(ActivityMemberModel) this.activityManageDao.getActivityMember(activityId, stuNumber);
									ActivityBaseinfoModel activityPo=(ActivityBaseinfoModel) this.activityManageDao.get(ActivityBaseinfoModel.class, activityId);
									if(activityMemberModel==null){
										activityMember.setMember(studentInfo);
										activityMember.setActivityPo(activityPo);
										activityMember.setRegistraForm(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE"));
										activityMember.setDeleteStatus(Constants.STATUS_NORMAL);
										activityMember.setApproveStatus(dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
										message=this.saveActivityMember(activityMember);
									}else{
										//判断学生是否通过，若通过则不修改
										if(activityMemberModel.getApproveStatus()!=null && activityMemberModel.getApproveStatus().getId().equals(dicUtil.getDicInfo("APPLY_APPROVE", "PASS").getId())){
											message ="success";	
										}else{
											this.activityManageDao.updateActivityMember(activityId, stuNumber, dicUtil.getDicInfo("APPLY_APPROVE", "PASS").getId(), null);
											message ="success";	
										}
										//message ="学号为"+stuNumber+"的学生已报名该活动，请确认后再重新上传！";	
									}
								}else{
									message ="学号为"+stuNumber+"的学生在系统中不存在，请确认后再重新上传！";	
								}
							}
						}
				} 
				return message;		
	}
	/** 
	* @Title: queryActivityMemberList 
	* @Description: 活动参与人员信息列表
	* @param  @param ActivityMemberModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return List<ActivityMemberModel>    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryActivityMemberList(String activityId) {
		return this.activityManageDao.queryActivityMemberList(activityId);
	}
	/** 
	* @Title: queryOffActivityMemberList 
	* @Description: 活动参与人员信息列表(线下报名)
	* @param  @param ActivityMemberModel    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryOffActivityMemberList(String activityId){
		return this.activityManageDao.queryOffActivityMemberList(activityId);
	}
	/** 
	* @Title: queryActivityPassMemberList 
	* @Description: 活动已通过参与人员信息列表
	* @param  @param ActivityMemberModel
	* @param  @param pageNo
	* @param  @param pageSize
	* @param  @return    
	* @return List<ActivityMemberModel>    
	* @throws 
	*/
	@Override
	public List<ActivityMemberModel> queryActivityPassMemberList(String activityId){
		return this.activityManageDao.queryActivityPassMemberList(activityId);
	}
	/** 
	* @Title: queryActivityByMemberIdList 
	* @Description: 查询当前登录人已报名或已申请的活动信息列表
	* @param  @param ActivityBaseinfoModel  
	* @return String[]    
	* @throws 
	*/
	@Override
	public String[] queryActivityByMemberIdList(String userId){
		List<ActivityBaseinfoModel> activity = this.activityManageDao.queryActivityByMemberIdList(userId);
		int len = null == activity ? 0 : activity.size();
		if(len > 0){
			String[] objectIds = new String[len];
			for(int i=0;i<len;i++)
				objectIds[i]= activity.get(i).getId();
			return objectIds;
		}
		return new String[]{"1"};
	}
	/*----------工作人员维护------------*/
	
	/** 
	* @Title: queryActivityWorkerList 
	* @Description: 根据活动id查询活动工作人员列表
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public List<ActivityWorkerModel> queryActivityWorkerList(String activityId) {
		return this.activityManageDao.queryActivityWorkerList(activityId);
	}
	/** 
	* @Title: queryActivityWorkers 
	* @Description: 根据活动id查询活动工作人员page
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public Page queryActivityWorkersPage(int pageNo,int pageSize,String activityId){
		return this.activityManageDao.queryActivityWorkersPage(pageNo, pageSize, activityId);
	}
	/** 
	* @Title: deleteActivityWorkers 
	* @Description: 根据活动id删除所有活动工作人员
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivityWorkers(String activityId){
		this.activityManageDao.deleteActivityWorker(activityId);
	}
	/*----------带队教师维护------------*/
	/** 
	* @Title: queryActivityTeacherList 
	* @Description: 活动带队老师信息列表
	* @return List<ActivityTeacherModel>    
	* @throws 
	*/
	@Override
	public List<ActivityTeacherModel> queryActivityTeacherList(String activityId) {
		return this.activityManageDao.queryActivityTeacherList(activityId);
	}
	/** 
	* @Title: queryActivityTeacherPage 
	* @Description: 根据活动id查询活动带队教师page
	* @param  @return    
	* @return   Page
	* @throws 
	*/
	@Override
	public Page queryActivityTeacherPage(int pageNo,int pageSize,String activityId){
		return this.activityManageDao.queryActivityTeacherPage(pageNo, pageSize, activityId);
	}
	/** 
	* @Title: deleteActivityTeachers 
	* @Description: 根据活动id删除所有活动带队教师
	* @param  @return    
	* @return   
	* @throws 
	*/
	@Override
	public void deleteActivityTeachers(String activityId){
		this.activityManageDao.deleteActivityTeacher(activityId);
	}
	/**
	 * 根据id获取学院、专业、班级的字符串
	 * @param ids
	 * @param type
	 * @return
	 */
	@Override
	public String getAllNameStrByIds(String ids, String type) {
		String[] arr=ids.split(",");
		String str="";
		for(int i=0;i<arr.length;i++){
			if("collegeIds".equals(type)){
				if(i==(arr.length-1)){
					str=str+this.baseDataService.findAcademyById(arr[i]).getName();
				}else{
					str=str+this.baseDataService.findAcademyById(arr[i]).getName()+",";
				}
				
			}else if("majorIds".equals(type)){
				if(i==(arr.length-1)){
					str=str+this.baseDataService.findMajorById(arr[i]).getMajorName();
				}else{
					str=str+this.baseDataService.findMajorById(arr[i]).getMajorName()+",";
				}
			}else if("classIds".equals(type)){
				if(i==(arr.length-1)){
					str=str+this.baseDataService.findClassById(arr[i]).getClassName();
				}else{
					str=str+this.baseDataService.findClassById(arr[i]).getClassName()+",";
				}
			}
				
		}
		return str;
	}
}
