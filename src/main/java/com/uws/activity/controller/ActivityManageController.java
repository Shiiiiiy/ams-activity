package com.uws.activity.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.activity.service.IActivityManageService;
import com.uws.activity.util.ActivityConstant;
import com.uws.common.service.IActivityService;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.Constants;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.StringUtils;
import com.uws.domain.activity.ActivityBaseinfoModel;
import com.uws.domain.activity.ActivityMemberModel;
import com.uws.domain.activity.ActivityTeacherModel;
import com.uws.domain.activity.ActivityWorkerModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseClassModel;
import com.uws.domain.base.BaseMajorModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.volunteer.VolunteerOfficeModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.model.User;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName:ActivityManageController
* @Description:活动维护控制类Controller
* @author
* @date
*/
@Controller
public class ActivityManageController extends BaseController{

	@Autowired
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private Logger log = new LoggerFactory(ActivityManageController.class);
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	private FileUtil fileUtil=FileFactory.getFileUtil();
	@Autowired
	private IActivityManageService activityManageService;
	// 基础数据service
	@Autowired
	private IBaseDataService baseDataService;
	//组件封装service
	@Autowired
	private ICompService compService;
	@Autowired
	private IStudentCommonService studentCommonServie;
	//共青团  活动所用接口
	@Autowired
	private IActivityService activityService;
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	/** 
	* @Title: ListManageActivity 
	* @Description: 活动维护列表页
	* @param model
	* @param request
	* @param activity 活动对象
	* @return String
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/listManageActivity")
	public String ListManageActivity(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		
		log.info("Controller:ActivityManageController;方法:活动维护查询列表ListManageActivity()");
        
		//获取当前登录人id
		String userId = sessionUtil.getCurrentUserId();
		model.addAttribute("activity", activity);
		//根据当前登录人查询需要维护的活动列表
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		Page page=activityManageService.queryManageActivityPage(activity, pageNo, Page.DEFAULT_PAGE_SIZE, userId);
		List<ActivityBaseinfoModel> newResultList = new ArrayList<ActivityBaseinfoModel>();
 		List<ActivityBaseinfoModel> resultList = (List<ActivityBaseinfoModel>)page.getResult();
 		for(ActivityBaseinfoModel a:resultList){
 			if(a!=null&&a.getCollegeIds()!=null&&!"".equals(a.getCollegeIds())){
 				String collegeStr=this.activityManageService.getAllNameStrByIds(a.getCollegeIds(),"collegeIds");
 				a.setCollegeIds(collegeStr);
 			}
 			if(a!=null&&a.getMajorIds()!=null&&!"".equals(a.getMajorIds())){
 				String majorStr=this.activityManageService.getAllNameStrByIds(a.getMajorIds(),"majorIds");
 				a.setMajorIds(majorStr);
 			}
 			if(a!=null&&a.getClassIds()!=null&&!"".equals(a.getClassIds())){
 				String calssStr=this.activityManageService.getAllNameStrByIds(a.getClassIds(),"classIds");
 				a.setClassIds(calssStr);
 			}
 			newResultList.add(a);
 		}
 		page.setResult(newResultList);
		List<Dic> activityTypeDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE");
		List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
		List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
		//活动报名方式（线上，线下）
		List<Dic> activityRegistraFormDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_REGISTRA_FORM");
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != activity && null != activity.getCollegeIds()&& activity.getCollegeIds().length() > 0) {
			majorList = compService.queryMajorByCollage(activity.getCollegeIds());
			log.debug("若已经选择学院，则查询学院下的专业信息.");
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != activity && null != activity.getClassIds()  && activity.getMajorIds().length() > 0) {
			classList = compService.queryClassByMajor(activity.getMajorIds());
			log.debug("若已经选择专业，则查询专业下的班级信息.");
		}
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("activityTypeDicList", activityTypeDicList);
		model.addAttribute("activityCategoryDicList", activityCategoryDicList);
		model.addAttribute("activityLevelDicList", activityLevelDicList);
		model.addAttribute("activityRegistraFormDicList", activityRegistraFormDicList);

		model.addAttribute("page", page);
		return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityManageList";	
	}
	
	/**
	* @Title: editActivityinfo 
	* @Description: 新增/修改活动跳转
	* @param model
	* @param request
	* @param activity
	* @return String   
	*/
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-add/addActivity",ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-edit/editActivity"})
	public String editActivityinfo(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		log.info("Controller:ActivityManageController;方法:学生查询列表editActivityinfo()");
		String id = (String) request.getParameter("id");
		String userId = sessionUtil.getCurrentUserId();
		StudentInfoModel student=studentCommonServie.queryStudentById(userId);
		List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
		List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
		//活动类型
		List<Dic> activityTypeDicList = new ArrayList<Dic>();
		activityTypeDicList.add(dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_SOCIAL_PRACTICE"));
		List<AssociationBaseinfoModel> associationList=activityService.queryAssociationByMemberId(userId);
		if(activityService.queryAssociationByMemberId(userId).size()>0){
			//activityTypeDicList.add(dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_ASSOCIATION"));
		}
		if(null!=activityService.queryLeagueMember(userId)){
			//activityTypeDicList.add(dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_LEAGUE"));
		}
      //  if(null!=activityService.queryVolunteer(userId)){
        	//activityTypeDicList.add(dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_VOLUNTEER_SERVICE"));
        	List<VolunteerOfficeModel> volunteerList=activityService.queryVolunteerOfficeList(null);
        	List<VolunteerOfficeModel> volunteer=activityService.queryVolunteerOfficeList((student!=null&&student.getCollege()!=null)?student.getCollege().getId():"1");
			model.addAttribute("volunteerList", volunteerList);
			model.addAttribute("volunteer", volunteer.size() > 0 ? volunteer.get(0) : null);
     //   }
       // activityTypeDicList.add(dicUtil.getDicInfo("HKY_ACTIVITY_TYPE", "ACTIVITY_OTHER"));

		List<Dic> activityRegistraFormDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_REGISTRA_FORM");

		if (StringUtils.hasText(id)) {
			activity = activityManageService.queryActivity(id);
			List<ActivityTeacherModel> teacherlist=activityManageService.queryActivityTeacherList(id);
			String teacherIds = null;
			String teacherNames = null;
			for(int i=0;i<teacherlist.size();i++){
				if(teacherlist.get(i).getLeaderTeacher()!=null){
					if(i==0){
						teacherIds=teacherlist.get(0).getLeaderTeacher().getId();
						teacherNames=teacherlist.get(0).getLeaderTeacher().getName();
					}else{
						teacherIds=teacherIds+","+teacherlist.get(i).getLeaderTeacher().getId();
						teacherNames=teacherNames+","+teacherlist.get(i).getLeaderTeacher().getName();
					}
				}
			}
			List<ActivityWorkerModel> workerlist=activityManageService.queryActivityWorkerList(id);
			String workerIds = null;
			String workerNames = null;
			for(int j=0;j<workerlist.size();j++){
				if(workerlist.get(j).getWorkerPo()!=null){
					if(j==0){
						workerIds=workerlist.get(0).getWorkerPo().getId();
						workerNames=workerlist.get(0).getWorkerPo().getName();
					}else{
						workerIds=workerIds+","+workerlist.get(j).getWorkerPo().getId();
					    workerNames=workerNames+";"+workerlist.get(j).getWorkerPo().getName();
					}
				}
			}
			model.addAttribute("workerIds", workerIds);
			model.addAttribute("workerNames", workerNames);
			model.addAttribute("teacherIds", teacherIds);
			model.addAttribute("teacherNames", teacherNames);
		}
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != activity && null != activity.getCollegeIds()&& activity.getCollegeIds().length() > 0) {
			majorList = compService.queryMajorByCollage(activity.getCollegeIds());
			log.debug("若已经选择学院，则查询学院下的专业信息.");
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != activity && null != activity.getMajorIds()  && activity.getMajorIds().length() > 0) {
			classList = compService.queryClassByMajor(activity.getMajorIds());
			log.debug("若已经选择专业，则查询专业下的班级信息.");
		}
		model.addAttribute("associationList", associationList);
		model.addAttribute("student", student);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("activityTypeDicList", dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE"));
		model.addAttribute("activityCategoryDicList", activityCategoryDicList);
		model.addAttribute("activityLevelDicList", activityLevelDicList);
		model.addAttribute("activityRegistraFormDicList", activityRegistraFormDicList);
		model.addAttribute("activity", activity);
		return  ActivityConstant.ACTIVITY_BASE_MANAGE_FTL + "/activityEdit";			
	}
	/** 
	* @title saveActivity
	* @Description: 走审批流保存活动信息(返回活动id)
	* @param model
	* @param request
	* @param activity 活动对象
	* @return String   
	*/
	@ResponseBody
	@RequestMapping(value = {ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-save/saveActivity" },produces = { "text/plain;charset=UTF-8"})
	public String saveActivity(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		log.info("Controller:ActivityManageController;方法:学生查询列表submitActivityinfo()");
		//活动保存时要提交活动基本信息，活动工作人员，活动带队老师。要先对活动工作人员，活动带队老师表中信息进行删除，在对信息进行添加
		//获取当前登录人的id
		String userId = sessionUtil.getCurrentUserId();
		//工作人员
		String workerIds = (String) request.getParameter("workerIds");
		//带队老师
		String teacherIds = (String) request.getParameter("teacherIds");
		//根据id查询活动信息
		ActivityBaseinfoModel activityInfo=activityManageService.queryActivity(activity.getId());
		if(activityInfo!=null){
		    BeanUtils.copyProperties(activity,activityInfo,new String[]{"id","suggest","processStatus","approveResult","nextApprover","deleteStatus","status","createTime","creator","isSignStatus","summaryStatus"});
			activityManageService.updateActivity(activityInfo,workerIds,teacherIds);
		}else{
			//创建人
			StudentInfoModel user= this.studentCommonServie.queryStudentById(userId);
			activity.setCreator(user);
			//保存状态
			activity.setStatus(Constants.OPERATE_STATUS.SAVE.toString());
			//删除状态
			activity.setDeleteStatus(Constants.STATUS_NORMAL);
			//活动总结状态
			activity.setSummaryStatus("NORMAL");
			//可报名状态
			activity.setIsSignStatus(Constants.STATUS_NO);
			activityManageService.saveActivity(activity,workerIds,teacherIds);
		}
			return activity.getId();
	}
	/**
	* @title saveActivityinfo
	* @Description: 保存活动信息（跳转到列表页面）
	* @param model
	* @param request
	* @param activity
	* @return String
	*/
	@RequestMapping(value = {ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-save/saveActivityinfo" })
	public String saveActivityinfo(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		log.info("Controller:ActivityManageController;方法:学生查询列表submitActivityinfo()");
		//获取当前登录人的id
		String userId = sessionUtil.getCurrentUserId();
		//活动组织部门联系人（选的是老师还是学生）
		String contactType = request.getParameter("contactType");
		//工作人员
		String workerIds = (String) request.getParameter("workerIds");
		//带队老师
		String teacherIds = (String) request.getParameter("teacherIds");
		//根据id查询活动信息
		ActivityBaseinfoModel activityInfo=activityManageService.queryActivity(activity.getId());
		//活动组织部门联系人（选的是老师还是学生，如果选的是老师，则把学生置空，否则把学生置空）
		if(activityInfo!=null){
		    BeanUtils.copyProperties(activity,activityInfo,new String[]{"id","suggest","processStatus","approveResult","nextApprover","deleteStatus","status","createTime","creator","isSignStatus","summaryStatus"});
		    if(contactType !=null && StringUtils.hasText(contactType) && contactType.equals("student"))
		    {
		    	activityInfo.setContactTeachers(null);
		    }else
		    {
		    	activityInfo.setContactStudents(null);
		    }
			activityManageService.updateActivity(activityInfo,workerIds,teacherIds);
		}else{
			//创建人
			StudentInfoModel user= this.studentCommonServie.queryStudentById(userId);
			activity.setCreator(user);
			//保存状态
			activity.setStatus(Constants.OPERATE_STATUS.SAVE.toString());
			//删除状态
			activity.setDeleteStatus(Constants.STATUS_NORMAL);
			//可报名状态
			activity.setIsSignStatus(Constants.STATUS_NO);
			activity.setSummaryStatus("NORMAL");

			activityManageService.saveActivity(activity,workerIds,teacherIds);
		}
			return "redirect:"+ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/listManageActivity.do";  			
	}
	/**
	* @title submitActivityinfo
	* @Description: 不走审批流提交活动信息
	* @param model
	* @param request
	* @param activity 活动对象
	* @return String
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-submit/submitActivity")
	public String submitActivityinfo(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		log.info("Controller:ActivityManageController;方法:学生查询列表submitActivityinfo()");
		//活动保存时要提交活动基本信息，活动工作人员，活动带队老师。要先对活动工作人员，活动带队老师表中信息进行删除，在对信息进行添加
		//获取当前登录人的id
		String userId = sessionUtil.getCurrentUserId();
		//活动组织部门联系人（选的是老师还是学生）
		String contactType = request.getParameter("contactType");
		//工作人员
		String workerIds = (String) request.getParameter("workerIds");
		//带队老师
		String teacherIds = (String) request.getParameter("teacherIds");
		//ACTIVITY_OFFLINE
		if(activity.getRegistraForm()!=null && (activity.getRegistraForm().getId().equals(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_ONLINE").getId()))){
			//可报名
			activity.setIsSignStatus(Constants.STATUS_YES);
		}else{
			//不报名
			activity.setIsSignStatus(Constants.STATUS_NO);
		}
		//根据id查询活动信息
		ActivityBaseinfoModel activityInfo=activityManageService.queryActivity(activity.getId());
		if(activityInfo!=null){
			//状态为提交
			activity.setProcessStatus("PASS");
			activity.setStatus(Constants.OPERATE_STATUS.SUBMIT.toString());
		    BeanUtils.copyProperties(activity,activityInfo,new String[]{"id","suggest","approveResult","nextApprover","deleteStatus","createTime","creator","summaryStatus"});
		  //活动组织部门联系人（选的是老师还是学生，如果选的是老师，则把学生置空，否则把学生置空）
		    if(contactType !=null && StringUtils.hasText(contactType) && contactType.equals("student"))
		    {
		    	activityInfo.setContactTeachers(null);
		    }else
		    {
		    	activityInfo.setContactStudents(null);
		    }
		    activityManageService.updateActivity(activityInfo,workerIds,teacherIds);
		}else{
			//创建人
			StudentInfoModel user= this.studentCommonServie.queryStudentById(userId);
			activity.setCreator(user);
			activity.setProcessStatus("PASS");
			//保存状态
			activity.setStatus(Constants.OPERATE_STATUS.SUBMIT.toString());
			activity.setSummaryStatus("NORMAL");

			//删除状态
			activity.setDeleteStatus(Constants.STATUS_NORMAL);
			
			activityManageService.saveActivity(activity,workerIds,teacherIds);
		}
	   return "redirect:"+ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/listManageActivity.do";			
	}	

	/**
	 * @title queryManageActivity
	 * @Description: 活动维护,活动审核的信息查询方法
	 * @param model
	 * @param request
	 * @param response
	 * @param status 区分跳转页面信息标记字段
	 * @param activityId 活动对象id
	 * @return 
	 */
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/queryManageActivity",ActivityConstant.ACTIVITY_BASE_APPROVE + "/opt-query/queryApproveActivity"})
	public String queryManageActivity(ModelMap model, HttpServletRequest request,HttpServletResponse response, String status, String activityId) {
		
		log.info("Controller:ActivityManageController;方法:学生查询列表queryActivity()");

		if (StringUtils.hasText(activityId)) {
			ActivityBaseinfoModel activity = activityManageService.queryActivity(activityId);
			List<ActivityTeacherModel> teacherlist=activityManageService.queryActivityTeacherList(activityId);
			String teacherIds = null;
			String teacherNames = null;
			for(int i=0;i<teacherlist.size();i++){
				if(teacherlist.get(i).getLeaderTeacher()!=null){
					if(i==0){
						teacherIds=teacherlist.get(0).getLeaderTeacher().getId();
						teacherNames=teacherlist.get(0).getLeaderTeacher().getName();
					}else{
						teacherIds=teacherIds+","+teacherlist.get(i).getLeaderTeacher().getId();
						teacherNames=teacherNames+";"+teacherlist.get(i).getLeaderTeacher().getName();
					}
				}
			}
			
			List<ActivityWorkerModel> workerlist=activityManageService.queryActivityWorkerList(activityId);
			String workerIds = null;
			String workerNames = null;
			for(int j=0;j<workerlist.size();j++){
				if(workerlist.get(j).getWorkerPo()!=null){
					if(j==0){
						workerIds=workerlist.get(0).getWorkerPo().getId();
						workerNames=workerlist.get(0).getWorkerPo().getName();
					}else{
						workerIds=workerIds+","+workerlist.get(j).getWorkerPo().getId();
					    workerNames=workerNames+","+workerlist.get(j).getWorkerPo().getName();
					}
				}
			}
			if(activity!=null&&activity.getCollegeIds()!=null&&!"".equals(activity.getCollegeIds())){
 				String collegeStr=this.activityManageService.getAllNameStrByIds(activity.getCollegeIds(),"collegeIds");
 				activity.setCollegeIds(collegeStr);
 			}
 			if(activity!=null&&activity.getMajorIds()!=null&&!"".equals(activity.getMajorIds())){
 				String majorStr=this.activityManageService.getAllNameStrByIds(activity.getMajorIds(),"majorIds");
 				activity.setMajorIds(majorStr);
 			}
 			if(activity!=null&&activity.getClassIds()!=null&&!"".equals(activity.getClassIds())){
 				String calssStr=this.activityManageService.getAllNameStrByIds(activity.getClassIds(),"classIds");
 				activity.setClassIds(calssStr);
 			}
			model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(activityId));
			model.addAttribute("workerIds", workerIds);
			model.addAttribute("workerNames", workerNames);
			model.addAttribute("teacherIds", teacherIds);
			model.addAttribute("teacherNames", teacherNames);
			model.addAttribute("activity", activity);

			//活动是否举行
		    List<Dic> holdStatusDicList = dicUtil.getDicInfoList("Y&N");
			model.addAttribute("holdStatusDicList", holdStatusDicList);
			if(StringUtils.hasText(status)){
				model.addAttribute("status", "approve");
			}else{
				if(Constants.OPERATE_STATUS.SUBMIT.toString().equals(activity.getStatus())&&(activity.getNextApprover()!=null || StringUtils.hasText(activity.getSuggest()) || StringUtils.hasText(activity.getApproveResult()))){
					model.addAttribute("noApproveStatus", "noApprove");
				}
			}
		}
		return ActivityConstant.ACTIVITY_BASE_FTL + "/activityView";
	}
	
	/**
	* @title addActivitySummary
	* @Description: 添加活动总结跳转
	* @param model
	* @param request
	* @param activityId 活动对象id
	* @return String    
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-add/addActivitySummary")
	public String addActivitySummary(ModelMap model,HttpServletRequest request,String activityId) {
		
		log.info("Controller:ActivityManageController;方法:添加活动总结跳转addActivitySummary()");
		ActivityBaseinfoModel activity=this.activityManageService.queryActivity(activityId);
		//活动是否举行
		List<Dic> holdStatusDicList = dicUtil.getDicInfoList("Y&N");
		List<ActivityTeacherModel> teacherlist=activityManageService.queryActivityTeacherList(activityId);
		String teacherIds = null;
		String teacherNames = null;
		for(int i=0;i<teacherlist.size();i++){
			if(teacherlist.get(i).getLeaderTeacher()!=null){
				if(i==0){
					teacherIds=teacherlist.get(0).getLeaderTeacher().getId();
					teacherNames=teacherlist.get(0).getLeaderTeacher().getName();
				}else{
					teacherIds=teacherIds+","+teacherlist.get(i).getLeaderTeacher().getId();
					teacherNames=teacherNames+";"+teacherlist.get(i).getLeaderTeacher().getName();
				}
			}
		}
		List<ActivityWorkerModel> workerlist=activityManageService.queryActivityWorkerList(activityId);
		String workerIds = null;
		String workerNames = null;
		for(int j=0;j<workerlist.size();j++){
			if(workerlist.get(j).getWorkerPo()!=null){
				if(j==0){
					workerIds=workerlist.get(0).getWorkerPo().getId();
					workerNames=workerlist.get(0).getWorkerPo().getName();
				}else{
					workerIds=workerIds+","+workerlist.get(j).getWorkerPo().getId();
				    workerNames=workerNames+","+workerlist.get(j).getWorkerPo().getName();
				}
			}
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		Page page=activityManageService.queryActivityWorkersPage(pageNo, 5, activityId);
		model.addAttribute("page", page);
		model.addAttribute("workerIds", workerIds);
		model.addAttribute("workerNames", workerNames);
		model.addAttribute("teacherIds", teacherIds);
		model.addAttribute("teacherNames", teacherNames);
		model.addAttribute("holdStatusDicList", holdStatusDicList);
		model.addAttribute("activity", activity);
		model.addAttribute("uploadFileRefList", this.fileUtil.getFileRefsByObjectId(activityId));
		return  ActivityConstant.ACTIVITY_BASE_MANAGE_FTL + "/activitySummaryEdit";			
	}
	/**
	 * @title updateActivitySummary
	 * @Description: 添加活动总结 
	 * @param model
	 * @param request
	 * @param response
	 * @param activity 活动对象
	 * @param fileId 上传文件id
	 * @return String
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-edit/editActivitySummary"})
	public String updateActivitySummary(ModelMap model, HttpServletRequest request, HttpServletResponse response, ActivityBaseinfoModel activity, String[] fileId){

		log.info("Controller:ActivityManageController;方法:修改活动总结:updateActivitySummary()");
		if(activity!=null && StringUtils.hasText(activity.getId())){
		   this.activityManageService.updateActivitySummary(activity,fileId);
		}
		return "redirect:"+ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/listManageActivity.do";
	}
	/**
	 * @title updateActivitySignStatus
	 * @Description: 修改报名状态 
	 * @param id
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-edit/editActivitySignStatus"} , produces={"text/plain;charset=UTF-8"})
	public String updateActivitySignStatus(String id){

		log.info("Controller:ActivityManageController;方法:修改报名状态updateActivitySignStatus()");
		ActivityBaseinfoModel activity = activityManageService.queryActivity(id);
		String a=null;
		if (activity!=null && activity.getIsSignStatus()!=null){
			if((Constants.STATUS_NO.getId()).equals(activity.getIsSignStatus().getId())){
				this.activityManageService.updateActivitySignStatus(id,Constants.STATUS_YES.getId());
			}else{
				this.activityManageService.updateActivitySignStatus(id,Constants.STATUS_NO.getId());	
			}
		    a="success";
		}
			return a;
	}

	/**----------- 人员维护  -----------*/
	/**
	* @title addActivityMembers
	* @Description: 添加活动参与人员
	* @param model
	* @param request
	* @param activity
	* @return String    
	*/
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-add/addActivityMembers"} , produces={"text/plain;charset=UTF-8"})
	public String addActivityMembers(ModelMap model,HttpServletRequest request,String activityId, String studentIds) {
		log.info("Controller:ActivityManageController;方法:新增活动参与人员addActivityMembers()");
		try{
			this.activityManageService.saveActivityMembers(activityId,studentIds);
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	/**
	* @Title: deleteActivityMember 
	* @Description: 删除活动参与人员
	* @param model
	* @param request
	* @param activityId 活动对象id
	* @param ids 活动参与人员id
	* @return String    
	*/
	@ResponseBody
	@RequestMapping(value = {ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-delete/deleteActivityMember" },produces = { "text/plain;charset=UTF-8" })
	public String deleteActivityMember(ModelMap model,HttpServletRequest request,String activityId, String ids) {
		log.info("Controller:ActivityManageController;方法:删除参与人员deleteActivityMember()");
		try{
			this.activityManageService.deleteActivityMembers(activityId, ids);
			return "success";
		}catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	/**
	 * @Title: activityMemberList 
	 * @Description:查询参加活动的学生列表
	 * @param model
	 * @param request
	 * @param response
	 * @param activityMember 活动参与人员
	 * @param activityId 活动id
	 * @return String
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-query/activityMemberList"})
	public String activityMemberList(ModelMap model,HttpServletRequest request,HttpServletResponse response,ActivityMemberModel activityMember,String activityId){
		log.info("Controller:ActivityManageController;方法:查询参加活动的学生列表asynLoadStuList()");
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.activityManageService.queryActivityMemberPage(pageNo, Page.DEFAULT_PAGE_SIZE, activityMember,activityId);
		//查询所有线下报名的人员
		List<ActivityMemberModel> list=this.activityManageService.queryOffActivityMemberList(activityId);
		ActivityBaseinfoModel activity=this.activityManageService.queryActivity(activityId);
		//活动报名审核状态
		List<Dic> applyApproveList = dicUtil.getDicInfoList("APPLY_APPROVE");
		String members=null;
		for(int i=0;i<list.size();i++){
			if(i==0){
				members=list.get(i).getMember().getId();
			}else{
				members=members+","+list.get(i).getMember().getId();
			}
		}
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != activityMember && null != activityMember.getMember() && null != activityMember.getMember().getCollege()&& null != activityMember.getMember().getCollege().getId()&& activityMember.getMember().getCollege().getId().length() > 0) {
			majorList = compService.queryMajorByCollage(activityMember.getMember().getCollege().getId());
			log.debug("若已经选择学院，则查询学院下的专业信息.");
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != activityMember && null != activityMember.getMember() && null != activityMember.getMember().getClassId() && null != activityMember.getMember().getMajor() && null != activityMember.getMember().getMajor().getId() && activityMember.getMember().getMajor().getId().length() > 0) {
			classList = compService.queryClassByMajor(activityMember.getMember().getMajor().getId());
			log.debug("若已经选择专业，则查询专业下的班级信息.");
		}
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("applyApproveList",applyApproveList);
		model.addAttribute("memberApproveStatusList",Constants.applyApproveStatusList);
		model.addAttribute("members",members);
		model.addAttribute("page", page);
		model.addAttribute("activity", activity);
		model.addAttribute("activityId", activityId);
		model.addAttribute("activityMember", activityMember);
		return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityMemberList";
	}
	/**
	 * @Title: activityMemberImport 
	 * @Description: 跳转导入活动参与人员信息
	 * @param model
	 * @param request
	 * @param response
	 * @param activityId 活动对象id
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-import/activityMemberImport"})
	public String activityMemberImport(ModelMap model,HttpServletRequest request,HttpServletResponse response,String activityId){
		model.addAttribute("activityId",activityId);
		return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityMemberImport";
	}
	/**
	 * @Title: importActivityMember
	 * @Description: 导入活动参与人员信息
	 * @param model
	 * @param file
	 * @param maxSize 
	 * @param allowedExt
	 * @param request
	 * @param activityId 活动对象id
	 * @return String
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "finally" })
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_MANAGE+"/opt-modify/importActivityMember")
	public String importActivityMember(ModelMap model, @RequestParam("file")  MultipartFile file, String maxSize,String allowedExt,HttpServletRequest request,String activityId){
		
		log.info("Controller:ActivityManageController;方法:导入活动参与人员列表importActivityMember()");
		String errorText="";
        try {
		//构建文件验证对象
    	MultipartFileValidator validator = new MultipartFileValidator();
    	if(org.apache.commons.lang.StringUtils.isNotEmpty(allowedExt)){
    		validator.setAllowedExtStr(allowedExt.toLowerCase());
    	}
    	//设置文件大小
    	if(org.apache.commons.lang.StringUtils.isNotEmpty(maxSize)){
    		validator.setMaxSize(Long.valueOf(maxSize));//20M
    	}else{
    		validator.setMaxSize(1024*1024*20);//20M
    	}
		//调用验证框架自动验证数据
        String returnValue=validator.validate(file);             
        if(!returnValue.equals("")){
        	model.addAttribute("errorText",returnValue);
        	return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityMemberImport";
        }
        
        String tempFileId=fileUtil.saveSingleFile(true, file); 
        File tempFile=fileUtil.getTempRealFile(tempFileId);
			Map map = new HashMap();
		    String message = this.activityManageService.importActivityMember(tempFile.getAbsolutePath(), "importActivityMember", map, ActivityMemberModel.class, activityId);
		    
			if (message != null && !"".equals(message) && !"success".equals(message)) {
				errorText = message;
			}
		} catch (ExcelException e) { 
			errorText = e.getMessage();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			 e.printStackTrace();
			errorText = "请上传正确模板的Excel文件！";
		} finally {
			model.addAttribute("errorText", errorText);
			model.addAttribute("activityId", activityId);
	        return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityMemberImport";
		}
	}
	/**------- 人员维护结束   --------*/
	/**
	 * @Title: queryActivitySignMembers
	 * @Description: 判断参与人数是否已达到报名人数上限
	 * @param activityId 活动对象id
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-query/queryActivitySignMembers"} , produces={"text/plain;charset=UTF-8"})
	public String queryActivitySignMembers(String activityId){

		log.info("Controller:ActivityManageController;方法:修改报名状态updateActivitySignStatus()");
		List<ActivityMemberModel> activityMemberList = activityManageService.queryActivityMemberList(activityId);
        String activityMembers=activityMemberList.size()+"";
		return activityMembers;
	}
	/**
	 * @Title: applyActivitySignUp
	 * @Description: 审核活动参与人员
	 * @param model
	 * @param request
	 * @param activityId 活动对象id
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-add/approveActivityMember"} , produces={"text/plain;charset=UTF-8"})
	public String approveActivityMember(ModelMap model,HttpServletRequest request,HttpServletResponse response, String activityId, String memberIds, String approveStatus, String suggest){
		
			ActivityBaseinfoModel activity=(ActivityBaseinfoModel) this.activityManageService.queryActivity(activityId);
	        if(activity!=null){
				String result=this.activityManageService.updateActivityMembers(activityId, memberIds, approveStatus,suggest);
				return result;
				}
			return "该活动不存在";
    }
	/**
	 * @Title: queryActivityByActivityName
	 * @Description: 活动名称唯一性验证
	 * @param activityName 活动名称
	 * @param activityId 活动对象id
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-add/queryActivityByActivityName"} , produces={"text/plain;charset=UTF-8"})
	public String queryActivityByActivityName(String activityName, String activityId){
		ActivityBaseinfoModel activity =this.activityManageService.queryActivityByActivityName(activityName,activityId);
		if(activity!=null){
			return "error";
		}		
		return null;
    }
	/**
	 * @Title: deleteActivity
	 * @Description: 删除活动信息(可以删除保存的活动信息（未走审批流，仅保存）)
	 * @param model
	 * @param request
	 * @param activityId
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_MANAGE + "/opt-delete/deleteActivity"}, produces={"text/plain;charset=UTF-8"})
	public String deleteActivity(ModelMap model,HttpServletRequest request,String activityId) {
		
		log.info("Controller:ActivityManageController;方法:学生查询列表ListStatisticActivity()");
		ActivityBaseinfoModel activity = activityManageService.queryActivity(activityId);
		if(activity!=null){
			//删除该活动所有参与人员
			activityManageService.deleteActivityAllMember(activityId);
			//删除该活动所有工作人员
			activityManageService.deleteActivityWorkers(activityId);
			//删除该活动所有带队教师
			activityManageService.deleteActivityTeachers(activityId);
			//删除该活动信息
			activityManageService.deleteActivity(activityId);
			return null;			
		}else{
			return "error";		
		}
	}
}
