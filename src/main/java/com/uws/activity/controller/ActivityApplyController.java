package com.uws.activity.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.activity.service.IActivityManageService;
import com.uws.activity.util.ActivityConstant;
import com.uws.apw.model.ApproveResult;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.apw.util.JsonUtils;
import com.uws.common.service.IActivityService;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.Constants;
import com.uws.comp.service.ICompService;
import com.uws.core.base.BaseController;
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
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;
import com.uws.util.ProjectSessionUtils;

/** 
* @ClassName:ActivityManageController
* @Description:活动申请审核控制类Controller
* @author
* @date
*/
@Controller
public class ActivityApplyController  extends BaseController{
	
	@Autowired
	private IActivityManageService activityManageService;
	@Autowired
	private IFlowInstanceService flowInstanceService;
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
	
	//日志工具
	private Logger log = new LoggerFactory(ActivityApplyController.class);
   //数据字典工具
	private DicUtil dicUtil = DicFactory.getDicUtil();
	//会话工具
	private SessionUtil sessionUtil = SessionFactory.getSession(null);
	//文件处理工具
	private FileUtil fileUtil=FileFactory.getFileUtil();

	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	/** 
	* @Title: ListApproveActivity 
	* @Description: 活动审核列表页
	* @param model
	* @param request
	* @param activity 活动对象
	* @return String    
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_APPROVE + "/opt-query/listApproveActivity")
	public String ListApproveActivity(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		log.info("Controller:ActivityApplyController;方法:学生查询列表ListApproveActivity()");
        //获取当前登录人id
		String userId = sessionUtil.getCurrentUserId();
		//根据当前登录人查询需要审核的活动列表
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		String[] objectIds = flowInstanceService.getObjectIdByProcessKey(ActivityConstant.ACTIVITY_INFO_INSIDE_APPROVE,userId);
		String[] objectIds1 = flowInstanceService.getObjectIdByProcessKey(ActivityConstant.ACTIVITY_INFO_SCHOOL_APPROVE,userId);
		String[] objectIds2 = flowInstanceService.getObjectIdByProcessKey(ActivityConstant.ACTIVITY_INFO_COLLEGE_APPROVE,userId);
		String[] objectIds3 = flowInstanceService.getObjectIdByProcessKey(ActivityConstant.ACTIVITY_INFO_OUTSIDE_SCHOOL_APPROVE,userId);
		String[] objectIds4 = flowInstanceService.getObjectIdByProcessKey(ActivityConstant.ACTIVITY_INFO_OUTSIDE_COLLEGE_APPROVE,userId);
		Page page=activityManageService.queryApproveActivityPage(pageNo, Page.DEFAULT_PAGE_SIZE, activity, userId, objectIds, objectIds1,objectIds2,objectIds3,objectIds4);
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
		//活动类型(社团活动、社会实践、志愿服务、团学活动、其他)
		List<Dic> activityTypeDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE");
		//活动类别(校内活动、校外活动)
		List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
		//活动级别(学校级、学院级、社团级)	
		List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
		//活动报名方式（线上，线下）
		List<Dic> activityRegistraFormDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_REGISTRA_FORM");
		
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		
		if (null != activity && null != activity.getCollegeIds()&&  activity.getCollegeIds().length() > 0) {
			majorList = compService.queryMajorByCollage(activity.getCollegeIds());
			log.debug("若已经选择学院，则查询学院下的专业信息.");
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != activity.getMajorIds() && null != activity.getMajorIds() && activity.getMajorIds().length() > 0) {
			classList = compService.queryClassByMajor(activity.getMajorIds());
			log.debug("若已经选择专业，则查询专业下的班级信息.");
		}
		model.addAttribute("userId", userId);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("activityTypeDicList", activityTypeDicList);
		model.addAttribute("activityCategoryDicList", activityCategoryDicList);
		model.addAttribute("activityLevelDicList", activityLevelDicList);
		model.addAttribute("activityRegistraFormDicList", activityRegistraFormDicList);
		model.addAttribute("processStatusMap", ProjectSessionUtils.getApproveProcessStatus());
		model.addAttribute("activity", activity);	
		model.addAttribute("page", page);
		return ActivityConstant.ACTIVITY_BASE_APPROVE_FTL+"/activityApproveList";	
		}
	/** 
	* @Title: ListSignUpActivity 
	* @Description: 可申请活动列表页
	* @param model
	* @param request
	* @param activity 活动对象
	* @return String    
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_SIGNUP + "/opt-query/listSignUpActivity")
	public String ListSignUpActivity(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		
		log.info("Controller:ActivityApplyController;方法:学生查询列表ListSignUpActivity()");
		//获取当前登录人id
		String userId = sessionUtil.getCurrentUserId();
		StudentInfoModel student =studentCommonServie.queryStudentById(userId);
		if(student!=null){
			//活动类型(社团活动、社会实践、志愿服务、团学活动、其他)
			List<Dic> activityTypeDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE");
			//活动类别(校内活动、校外活动)
			List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
			//活动级别(学校级、学院级、社团级)	
			List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
			//活动报名方式（线上，线下）
			List<Dic> activityRegistraFormDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_REGISTRA_FORM");
			//不可报名的活动信息
			String[] objectIds = activityManageService.queryActivityByMemberIdList(userId);
			//查询已参与的社团列表
			String[] objectIds1;
			List<AssociationBaseinfoModel> association = activityService.queryAssociationByMemberId(userId);
			int len = null == association ? 0 : association.size();
			if(len > 0){
				objectIds1 = new String[len];
				for(int i=0;i<len;i++)
					objectIds1[i]= association.get(i).getId();
			}else{
				objectIds1=new String[]{"1"};
			}
			//String[] objectIds1 =activityService.queryAssociationByMemberId(userId);
			String volunteer=null;
			//判断是否为志愿者
			if(null!=activityService.queryVolunteer(userId)){
				volunteer="true";
			}
			String leagueMember=null;
			//判断是否为团员
			if(null!=activityService.queryLeagueMember(userId)){
				leagueMember="true";
			}
			int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
			Page page=activityManageService.querySignUpActivityPage(activity, pageNo, Page.DEFAULT_PAGE_SIZE, userId,student.getCollege().getId(), objectIds, objectIds1, volunteer, leagueMember);
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
			// 下拉列表 学院
			List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
			// 下拉列表 专业
			List<BaseMajorModel> majorList =null;
			if (null != activity && null != activity.getCollegeIds()&&  activity.getCollegeIds().length() > 0) {
				majorList = compService.queryMajorByCollage(activity.getCollegeIds());
				log.debug("若已经选择学院，则查询学院下的专业信息.");
			}
			// 下拉列表 班级
			List<BaseClassModel> classList =null;
			if (null != activity  && null != activity.getMajorIds()  && activity.getMajorIds().length() > 0) {
				classList = compService.queryClassByMajor(activity.getMajorIds());
				log.debug("若已经选择专业，则查询专业下的班级信息.");
			}
			model.addAttribute("collegeList", collegeList);
			model.addAttribute("majorList", majorList);
			model.addAttribute("classList", classList);
			model.addAttribute("activityTypeDicList", activityTypeDicList);
			model.addAttribute("activityCategoryDicList", activityCategoryDicList);
			model.addAttribute("activityLevelDicList", activityLevelDicList);
			model.addAttribute("activityRegistraFormDicList", activityRegistraFormDicList);
			model.addAttribute("activity", activity);
			model.addAttribute("page", page);
		}
		model.addAttribute("student", student);
		return ActivityConstant.ACTIVITY_BASE_FTL+"/activitySignUpList";			
	}
	/** 
	* @Title: ListActivitySelf 
	* @Description: 已申请的活动列表页
	* @param model
	* @param request
	* @param activityMember 活动参与人员
	* @return String    
	*/
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_INFO + "/opt-query/listSelfActivity")
	public String ListActivitySelf(ModelMap model,HttpServletRequest request,ActivityMemberModel activityMember) {
		log.info("Controller:ActivityApplyController;方法:学生查询列表ListActivitySelf()");
        //获取当前登录人id
		String userId = sessionUtil.getCurrentUserId();
		//活动类型(社团活动、社会实践、志愿服务、团学活动、其他)
		List<Dic> activityTypeDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE");
		//活动类别(校内活动、校外活动)
		List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
		//活动级别(学校级、学院级、社团级)	
		List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
		//活动报名方式（线上，线下）
		List<Dic> activityRegistraFormDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_REGISTRA_FORM");
		//活动报名审核状态
		List<Dic> applyApproveList = dicUtil.getDicInfoList("APPLY_APPROVE");

		//根据当前登录人查询需要审核的活动列表
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		Page page=activityManageService.querySelfActivityPage(activityMember,pageNo, Page.DEFAULT_PAGE_SIZE, userId);
		List<ActivityMemberModel> newResultList = new ArrayList<ActivityMemberModel>();
 		List<ActivityMemberModel> resultList = (List<ActivityMemberModel>)page.getResult();
 		for(ActivityMemberModel m:resultList){
 			if(m!=null&&m.getActivityPo()!=null&&m.getActivityPo().getCollegeIds()!=null&&!"".equals(m.getActivityPo().getCollegeIds())){
 				String collegeStr=this.activityManageService.getAllNameStrByIds(m.getActivityPo().getCollegeIds(),"collegeIds");
 				m.getActivityPo().setCollegeIds(collegeStr);
 			}
 			if(m!=null&&m.getActivityPo()!=null&&m.getActivityPo().getMajorIds()!=null&&!"".equals(m.getActivityPo().getMajorIds())){
 				String majorStr=this.activityManageService.getAllNameStrByIds(m.getActivityPo().getMajorIds(),"majorIds");
 				m.getActivityPo().setMajorIds(majorStr);
 			}
 			if(m!=null&&m.getActivityPo()!=null&&m.getActivityPo().getClassIds()!=null&&!"".equals(m.getActivityPo().getClassIds())){
 				String calssStr=this.activityManageService.getAllNameStrByIds(m.getActivityPo().getClassIds(),"classIds");

 				m.getActivityPo().setClassIds(calssStr);
 			}
 			newResultList.add(m);
 		}
 		page.setResult(newResultList);
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = baseDataService.listBaseAcademy();
		// 下拉列表 专业
		List<BaseMajorModel> majorList =null;
		if (null != activityMember && null != activityMember.getActivityPo() && null != activityMember.getActivityPo().getCollegeIds()&& activityMember.getActivityPo().getCollegeIds().length() > 0) {
			majorList = compService.queryMajorByCollage(activityMember.getActivityPo().getCollegeIds());
			log.debug("若已经选择学院，则查询学院下的专业信息.");
		}
		// 下拉列表 班级
		List<BaseClassModel> classList =null;
		if (null != activityMember && null != activityMember.getActivityPo() && null != activityMember.getActivityPo().getMajorIds()  && activityMember.getActivityPo().getMajorIds().length() > 0) {
			classList = compService.queryClassByMajor(activityMember.getActivityPo().getMajorIds());
			log.debug("若已经选择专业，则查询专业下的班级信息.");
		}
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("majorList", majorList);
		model.addAttribute("classList", classList);
		model.addAttribute("activityMember", activityMember);
		model.addAttribute("activityTypeDicList", activityTypeDicList);
		model.addAttribute("activityCategoryDicList", activityCategoryDicList);
		model.addAttribute("activityLevelDicList", activityLevelDicList);
		model.addAttribute("activityRegistraFormDicList", activityRegistraFormDicList);
		model.addAttribute("applyApproveList", applyApproveList);
		model.addAttribute("page", page);
		return ActivityConstant.ACTIVITY_BASE_FTL+"/activitySelfList";
	}
	/**
	 * @Title: asynLoadActivityWorkersList 
	 * @Description: 异步调用工作人员列表
	 * @param model
	 * @param request
	 * @param response
	 * @param activityId 活动对象id
	 * @return String
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_SIGNUP+"/nsm/loadActivityWorkersList"})
	public String asynLoadActivityWorkersList(ModelMap model,HttpServletRequest request,HttpServletResponse response,String activityId){

		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		Page page = this.activityManageService.queryActivityWorkersPage(pageNo, 5, activityId);
		model.addAttribute("page", page);
		ActivityBaseinfoModel activity = new ActivityBaseinfoModel();
		activity.setId(activityId);
		model.addAttribute("activity", activity);
		return ActivityConstant.ACTIVITY_BASE_FTL+"/activityInfo/activityInfoWorkers";
	}
	/**
	 * @Title: querySignUpActivity 
	 * @Description: 我的活动，活动报名的活动信息查询
	 * @param model
	 * @param request
	 * @param response
	 * @param status 标记字段用于区分报名列表查看和我的活动列表查看
	 * @param activityId 活动对象id
	 * @return String
	 */
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_INFO + "/opt-query/querySelfActivity",ActivityConstant.ACTIVITY_BASE_SIGNUP + "/opt-query/querySignUpActivity",ActivityConstant.ACTIVITY_BASE_STATISTIC + "/opt-query/queryStatisticActivity"})
	public String querySignUpActivity(ModelMap model, HttpServletRequest request,HttpServletResponse response, String status, String activityId) {
		
		log.info("Controller:ActivityApplyController;方法:学生查询列表queryActivity()");

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
			//工作人员分页
			int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
			Page page=activityManageService.queryActivityWorkersPage(pageNo, 5, activityId);
			model.addAttribute("page", page);
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
				model.addAttribute("status", "signUp");
			}else{
				if(("SUBMIT").equals(activity.getSummaryStatus())){
					model.addAttribute("status", "summary");
				}
			}
		}
			return ActivityConstant.ACTIVITY_BASE_FTL + "/activitySignUpView";				
	}
	/**
	 * @Title: editApproveActivity 
	 * @Description: 活动审核页面跳转
	 * @param model
	 * @param request
	 * @param response
	 * @return String
	 */
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_APPROVE + "/opt-edit/editApproveActivity")
	public String editApproveActivity(ModelMap model, HttpServletRequest request,HttpServletResponse response) {
		
		log.info("Controller:ActivityApplyController;方法:学生审核信息查询queryApproveStudent()");
		String userId = sessionUtil.getCurrentUserId();
		String activityId = (String) request.getParameter("activityId");
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
						teacherNames=teacherNames+","+teacherlist.get(i).getLeaderTeacher().getName();
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
					    workerNames=workerNames+";"+workerlist.get(j).getWorkerPo().getName();
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
			model.addAttribute("workerIds", workerIds);
			model.addAttribute("workerNames", workerNames);
			model.addAttribute("teacherIds", teacherIds);
			model.addAttribute("teacherNames", teacherNames);
			model.addAttribute("activity", activity);
			model.addAttribute("userId", userId);
		}
		return ActivityConstant.ACTIVITY_BASE_APPROVE_FTL + "/approveActivityEdit";
	}
	/**------------审批流开始-----------*/
	/**
	 * 初始化当前流程
	 * @Title: saveCurProcess
	 * @Description: 初始化当前流程
	 * @param model
	 * @param request
	 * @param objectId			业务主键
	 * @param nextApproverId	下一节点办理人
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value = {ActivityConstant.ACTIVITY_BASE_APPROVE+"/opt-add/saveCurProcess"},produces = { "text/plain;charset=UTF-8" })
	public String saveCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());//封装发起人
				User nextApprover = new User(nextApproverId);//封装第一级审核人
				result = this.activityManageService.submitApprove(objectId, initiator, nextApprover);
				//result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
			}
		}else{
			result.setResultFlag("deprecated");
	    }

		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	/**
	 * @Description: 保存当前审批操作
	 * @param model
	 * @param request
	 * @param objectId
	 * @param nextApproverId 下一节点审核人id
	 * @param approveStatus 审核状态
	 * @param processStatusCode 审批流状态
	 * @return String
	 */
	@RequestMapping(value = {ActivityConstant.ACTIVITY_BASE_APPROVE+"/opt-add/saveApproveAction" },produces = { "text/plain;charset=UTF-8" })
	@ResponseBody
	public String saveApproveAction(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId,String approveStatus,String processStatusCode){
		log.info("Controller:ActivityApplyController;方法:保存活动当前审批操作saveApproveAction()");
		//String approveReason = (String) request.getParameter("approveReason");
		ApproveResult result = new ApproveResult();
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				  result.setApproveStatus(approveStatus);
				  result.setApproveResultCode(processStatusCode);
				  this.activityManageService.saveActivityApproveResult(objectId,result,nextApproverId,null);
				  result.setResultFlag("success");
			} catch (Exception e) {
				result.setResultFlag("error");
				e.printStackTrace();
			}
		}else{
			result.setResultFlag("deprecated");
	    }
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	/**
	 * @Title: saveApproveReason
	 * @Description: 保存审核的信息（保存在自己表中 审核理由）
	 * @param model
	 * @param request
	 * @return String
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_APPROVE+"/opt-save/saveApproveReason"})
	public String saveApproveReason(ModelMap model,HttpServletRequest request){
		String id = request.getParameter("id");
		String approveReason = request.getParameter("approveReason");
		
		ActivityBaseinfoModel activity =this.activityManageService.queryActivity(id);
		
		activity.setSuggest(approveReason);
		if( "PASS".equals(activity.getProcessStatus())||"REJECT".equals(activity.getProcessStatus())){
			activity.setNextApprover(null);
		}
		this.activityManageService.updateActivityInfo(activity);
		return "redirect:"+ActivityConstant.ACTIVITY_BASE_APPROVE+"/opt-query/listApproveActivity.do";						
	}
	/**------------审批流结束-----------*/
	/**
	 * @Title: activityMemberViewList
	 * @Description: 查询参加活动的学生列表(仅查看)
	 * @param model
	 * @param request
	 * @param response
	 * @param activityMember 活动参与人员对象
	 * @param activityId 活动对象id
	 * @return String
	 */
	@RequestMapping({ActivityConstant.ACTIVITY_BASE_APPROVE+"/opt-query/activityMemberViewList"})
	public String activityMemberViewList(ModelMap model, HttpServletRequest request, HttpServletResponse response, ActivityMemberModel activityMember, String activityId){
		log.info("Controller:ActivityApplyController;方法:查询参加活动的学生列表(仅查看)activityMemberViewList()");
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		activityMember.setApproveStatus(dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
		Page page = this.activityManageService.queryActivityMemberPage(pageNo, Page.DEFAULT_PAGE_SIZE, activityMember,activityId);
		ActivityBaseinfoModel activity=this.activityManageService.queryActivity(activityId);
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
		model.addAttribute("page", page);
		model.addAttribute("activity", activity);
		model.addAttribute("activityId", activityId);
		model.addAttribute("activityMember", activityMember);
		return ActivityConstant.ACTIVITY_BASE_MANAGE_FTL+"/activityMemberViewList";
	}
	/**
	 * @Title: applyActivitySignUp
	 * @Description: 报名活动
	 * @param model
	 * @param request
	 * @param response
	 * @param activityId
	 * @return String
	 */
	@ResponseBody
	@RequestMapping(value={ActivityConstant.ACTIVITY_BASE_SIGNUP + "/opt-add/applyActivitySignUp"} , produces={"text/plain;charset=UTF-8"})
	public String applyActivitySignUp(ModelMap model,HttpServletRequest request,HttpServletResponse response,String activityId){
			String userId = sessionUtil.getCurrentUserId();
			ActivityBaseinfoModel activity=(ActivityBaseinfoModel) this.activityManageService.queryActivity(activityId);
			ActivityMemberModel activityMember=(ActivityMemberModel) this.activityManageService.getActivityMember(activityId, userId);
	        if(activity!=null && activityMember==null){
					ActivityMemberModel activityMemberModel=new ActivityMemberModel();
					activityMemberModel.setActivityPo(activity);
					activityMemberModel.setDeleteStatus(Constants.STATUS_NORMAL);
					activityMemberModel.setApproveStatus(dicUtil.getDicInfo("APPLY_APPROVE", "NOT_APPROVE"));
					activityMemberModel.setRegistraForm(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_ONLINE"));
					StudentInfoModel member=studentCommonServie.queryStudentById(userId);
					if(member!=null){
						activityMemberModel.setMember(member);
						String result=this.activityManageService.saveActivityMember(activityMemberModel);
						return result;
					}
				}
			return "error";	
    }
	/**
	 * @Title: ListStatisticActivity
	 * @Description: 活动管理监管分析
	 * @param model
	 * @param request
	 * @param activity 活动对象
	 * @return String
	 */
	@RequestMapping(ActivityConstant.ACTIVITY_BASE_STATISTIC + "/opt-query/listStatisticActivity")
	public String ListStatisticActivity(ModelMap model,HttpServletRequest request,ActivityBaseinfoModel activity) {
		
		log.info("Controller:ActivityApplyController;方法:学生查询列表ListStatisticActivity()");
        
		//根据当前登录人查询需要维护的活动列表
		int pageNo = request.getParameter("pageNo") != null ? Integer .valueOf(request.getParameter("pageNo")) : 1;
		Page page=activityManageService.queryStatisticActivityPage(activity, pageNo, Page.DEFAULT_PAGE_SIZE);
		Collection<ActivityBaseinfoModel> activityList = page.getResult();
		for(ActivityBaseinfoModel activityBase: activityList){
			activityBase.setMembers(activityManageService.queryActivityPassMemberList(activityBase.getId()).size());
			if(activityBase!=null&&activityBase.getCollegeIds()!=null&&!"".equals(activityBase.getCollegeIds())){
 				String collegeStr=this.activityManageService.getAllNameStrByIds(activityBase.getCollegeIds(),"collegeIds");
 				activityBase.setCollegeIds(collegeStr);
 			}
 			if(activityBase!=null&&activityBase.getMajorIds()!=null&&!"".equals(activityBase.getMajorIds())){
 				String majorStr=this.activityManageService.getAllNameStrByIds(activityBase.getMajorIds(),"majorIds");
 				activityBase.setMajorIds(majorStr);
 			}
 			if(activityBase!=null&&activityBase.getClassIds()!=null&&!"".equals(activityBase.getClassIds())){
 				String calssStr=this.activityManageService.getAllNameStrByIds(activityBase.getClassIds(),"classIds");
 				activityBase.setClassIds(calssStr);
 			}
		}
	
		List<Dic> activityTypeDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_TYPE");
		List<Dic> activityCategoryDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_CATEGORY");
		List<Dic> activityLevelDicList = dicUtil.getDicInfoList("HKY_ACTIVITY_LEVEL");
		
		model.addAttribute("activityTypeDicList", activityTypeDicList);
		model.addAttribute("activityCategoryDicList", activityCategoryDicList);
		model.addAttribute("activityLevelDicList", activityLevelDicList);
		model.addAttribute("activity", activity);	

		model.addAttribute("page", page);
		return ActivityConstant.ACTIVITY_BASE_FTL+"/activityStatisticList";		
	}
}
