package com.uws.activity.util;

 /**
 * 
 * @ClassName: ActivityConstant 
 * @Description: 活动管理模块
 * @author 联合永道
 * @date 2015-9-25
 * 
 */
public class ActivityConstant
{
	/**
	 * 活动信息服务 活动信息查询 控制链接
	 */
	public static final String ACTIVITY_BASE_INFO = "/activity/info";
	
	/**
	 * 活动信息服务 活动管理 控制链接
	 */
	public static final String ACTIVITY_BASE_MANAGE = "/activity/manage";
	
	/**
	 * 活动信息服务 活动审核管理 控制链接
	 */
	public static final String ACTIVITY_BASE_APPROVE = "/activity/approve";
	
	/**
	 * 活动信息服务 活动报名管理 控制链接
	 */
	public static final String ACTIVITY_BASE_SIGNUP = "/activity/signup";
	
	/**
	 * 活动信息服务 活动监管分析 控制链接
	 */
	public static final String ACTIVITY_BASE_STATISTIC = "/activity/statistic";
	
	/**
	 * 活动信息服务 活动管理FTL页面路径
	 */
	public static final String ACTIVITY_BASE_MANAGE_FTL = "/activity/manage";
	/**
	 * 活动信息服务 活动审核FTL页面路径
	 */
	public static final String ACTIVITY_BASE_APPROVE_FTL = "/activity/approve";
	/**
	 * 活动信息服务 活动监管分析，报名，我的活动 FTL页面路径
	 */
	public static final String ACTIVITY_BASE_FTL = "/activity/info";
	/**
	 * 活动信息服务 校内信息审核
	 */
	public static final String ACTIVITY_INFO_INSIDE_APPROVE = "ACTIVITY_INFO_INSIDE_APPROVE";
	
	/**
	 * 校级
	 */
	public static final String ACTIVITY_INFO_SCHOOL_APPROVE = "ACTIVITY_INFO_SCHOOL_APPROVE";
	/**
	 * 院级
	 */
	public static final String ACTIVITY_INFO_COLLEGE_APPROVE = "ACTIVITY_INFO_COLLEGE_APPROVE";

	/**
	 * 校外-校级审批
	 */
	public static final String ACTIVITY_INFO_OUTSIDE_SCHOOL_APPROVE = "ACTIVITY_INFO_OUTSIDE_SCHOOL_APPROVE";
	/**
	 * 校外-院级审批
	 */
	public static final String ACTIVITY_INFO_OUTSIDE_COLLEGE_APPROVE = "ACTIVITY_INFO_OUTSIDE_COLLEGE_APPROVE";
	
	
	public static enum IS_SIGN_STATUS{
		TRUE,FALSE
	}
}
