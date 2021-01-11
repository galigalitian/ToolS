package com.tools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 时间日期处理工具
 * @author lee
 *
 */
public final class DateUtils {
	
	private static Log log = LogFactory.getLog(DateUtils.class);
	public static String yearMonthPattern = "yyyyMM";	//日期格式
    public static String datePattern = "yyyy-MM-dd";	//日期格式
    public static String timePattern = datePattern + " HH:mm:ss";	//时间格式
    public static String minutePattern = datePattern + " HH:mm";	//精确到分格式
    public static String longminutePattern = "yyyyMMddHHmm";	//精确到分格式
    public static final String MAXDATE = "9999-12-31";	//最大日期
    public static final String MAXTIME = "9999-12-31 23:59:59";	//最大时间
    
    public static Long MINUTE = 60000L;
    public static Long HOUR = 3600000L;
    public static Long DAY = 86400000L;
    /**设置1970 1 1 00：00：00 之后第一个星期一的毫秒值*/
	private static long distance=0l;
	private static long WEEK_CYCLE=7*24*60*60*1000;
	
    /**
     * 得到系统格式化过的日期
     * 参数为日期类型
     * @Methods Name getDateStr
     * @Create In Jun 16, 2011 By lee
     * @param aDate
     * @return String
     */
    public static final String getDateStr(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(datePattern);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }
    /**
     * 得到年月日时分秒格式化过的日期
     * 参数为日期类型
     * @Methods Name getTimeStr
     * @Create In Jun 16, 2011 By lee
     * @param aDate
     * @return String
     */
    public static final String getTimeStr(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(timePattern);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }
    /**
     * 得到本月最后一天的日期
     * @Methods Name getLastDayOfMonth
     * @Create In Jun 16, 2011 By lee
     * @param sDate1
     * @return Date
     */
    @SuppressWarnings("deprecation")
	public static Date getLastDayOfMonth(Date sDate1)   {   
        Calendar cDay1 = Calendar.getInstance();   
        cDay1.setTime(sDate1);   
        final int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);   
        Date lastDate = cDay1.getTime();   
        lastDate.setDate(lastDay);   
        return lastDate;   
	}   
    
    /**
     * 得到本月第一天的日期
     * @Methods Name getFirstDayOfMonth
     * @Create In Jun 16, 2011 By lee
     * @param sDate1
     * @return Date
     */
    @SuppressWarnings("deprecation")
	public static Date getFirstDayOfMonth(Date sDate1)   {   
        Calendar cDay1 = Calendar.getInstance();   
        cDay1.setTime(sDate1);   
        final   int   lastDay   =   cDay1.getActualMinimum(Calendar.DAY_OF_MONTH);   
        Date   lastDate   =   cDay1.getTime();   
        lastDate.setDate(lastDay);   
        return   lastDate;   
	}   

    /**
     * 获取某一天是所在月的第几天
     * @param sDate1
     * @return
     */
    public static int getIndexDayOfMonth(Date sDate1)   {   
        Calendar cDay1 = Calendar.getInstance();   
        cDay1.setTime(sDate1);
        return cDay1.get(Calendar.DAY_OF_MONTH);
	}
    /**
     * 获取某一天是所在月的第几天
     * @param sDate1
     * @return
     */
    public static int getDayOfMonth(Date sDate1)   {   
    	int yearMonth = getYearMonth(sDate1);
    	int index = getIndexDayOfMonth(sDate1);
    	if(index < 10) {
    		return Integer.valueOf(yearMonth+"0"+index);
    	}else {
    		return Integer.valueOf(yearMonth+""+index);
    	}
	}
    
    /**
     * 根据提供日期格式及日期字符串加工日期
     * @Methods Name convertStringToDate
     * @Create In Jun 16, 2011 By lee
     * @param aMask	日期格式
     * @param strDate	需要格式化的日期
     * @return
     * @throws ParseException Date
     */
    public static final Date convertStringToDate(String aMask, String strDate) throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        if (log.isDebugEnabled()) {
            log.debug("converting '" + strDate + "' to date with mask '"
                      + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            //log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * 得到Calendar型当前日期
     * @Methods Name getToday
     * @Create In Jun 16, 2011 By lee
     * @return
     * @throws ParseException Calendar
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(datePattern);
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));
        return cal;
    }
    
    /**
     * 获取系统日期格式的日期
     * @Methods Name getCurrentDate
     * @Create In Jun 16, 2011 By lee
     * @return java.util.Date
     */
    public static java.util.Date getCurrentDate() {
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat(datePattern);
		String todayAsString = df.format(today);
		Calendar cal = new GregorianCalendar();
		cal.setTime(convertStringToDate(todayAsString));
		return (java.util.Date) cal.getTime();
	}
    
    
    /**
     * 获取系统日期格式的日期字符串
     * @Methods Name getCurrentDate
     * @Create In Jun 16, 2011 By lee
     * @return java.util.Date
     */
    public static String getCurrentDateStr() {
		return DateUtils.convertDateToString(DateUtils.getCurrentDate());
	}
    
    
    /**
     * 获取系统时间格式的日期
     * @Methods Name getCurrentDateTime
     * @Create In Jun 16, 2011 By lee
     * @return java.util.Date
     */
    public static java.util.Date getCurrentDateTime(){
   	 	Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateUtils.timePattern);
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
			cal.setTime(convertStringToDate(todayAsString));
        return (java.util.Date)cal.getTime();
   }
    
    /**
     * 获取系统时间格式的日期字符串
     * @Methods Name getCurrentDateTime
     * @Create In Jun 16, 2011 By lee
     * @return java.util.Date
     */
    public static String getCurrentDateTimeStr(){
   	 	return DateUtils.convertDateTimeToString(DateUtils.getCurrentDateTime());
   }
    
    
    /**
     * 将日期转为指定格式的字符串
     * @Methods Name getDateTime
     * @Create In Jun 16, 2011 By lee
     * @param aMask	日期格式
     * @param aDate	日期
     * @return String
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    /**
     * 将日期转为系统格式的（年月日）日期字符串
     * @Methods Name convertDateToString
     * @Create In Jun 16, 2011 By lee
     * @param aDate
     * @return String
     */
    public static final String convertDateToString(Date aDate) {
        return getDateTime(datePattern, aDate);
    }
    
    /**
     * 将日期转成年月日 时分秒格式的字符串
     * @Methods Name convertDateTimeToString
     * @Create In Jun 16, 2011 By lee
     * @param aDate
     * @return String
     */
    public static final String convertDateTimeToString(Date aDate) {
        return getDateTime(timePattern, aDate);
    }
    
    /**
     * 将日期转成年月日 时分秒格式的字符串
     * @Methods Name convertDateTimeToString
     * @Create In Jun 16, 2011 By lee
     * @param aDate
     * @return String
     */
    public static final String convertDateTimeToString(Date aDate,String pattern) {
        return getDateTime(pattern, aDate);
    }

    /**
     * 将字符串转为对应的日期
     * @Methods Name convertStringToDate
     * @Create In Jun 16, 2011 By lee
     * @param strDate
     * @return Date
     */
    public static Date convertStringToDate(String strDate) {
        Date aDate = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("converting date with pattern: " + datePattern);
            }
            String pattern = "\\d{4}[-|/]\\d{2}[-|/]\\d{2}[ ]\\d{2}[:]\\d{2}[:]\\d{2}";	
			if(strDate.matches(pattern)) {
				if(strDate.contains("/")) {
				    aDate = convertStringToDate("yyyy/MM/dd HH:mm:ss", strDate);
				} else {
				    aDate = convertStringToDate(timePattern, strDate);
				}
			} else {
				if(strDate.contains("/")) {
				    aDate = convertStringToDate("yyyy/MM/dd", strDate);
				} else {
				    aDate = convertStringToDate(datePattern, strDate);
				}
			}
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate
                      + "' to a date, throwing exception");
            pe.printStackTrace();

        }

        return aDate;
    }

    /**
     * 在指定日期增加天数得到日期
     * @Methods Name addDays
     * @Create In Jun 16, 2011 By lee
     * @param date
     * @param days
     * @return Date
     */
    public static Date addDays(Date date, int days) {
        return add(date, days, Calendar.DATE);
    }

    /**
     * 在指定日期增加月数得到日期
     * @Methods Name addMonths
     * @Create In Jun 16, 2011 By lee
     * @param date
     * @param months
     * @return Date
     */
    public static Date addMonths(Date date, int months) {
        return add(date, months, Calendar.MONTH);
    }
    
    /**
     * 在指定日期增加年数得到日期
     * @param date
     * @param years
     * @return
     */
    public static Date addYears(Date date, int years) {
    	return add(date, years, Calendar.YEAR);
    }
    
    /**
     * 提供日期自处理方法，
     * @Methods Name add
     * @Create In Jun 16, 2011 By lee
     * @param date
     * @param amount	需要增加或减少的量
     * @param field		需要增加或减少的参数（年/月/日）
     * @return Date
     */
    public static Date add(Date date, int amount, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 是否是同一天
     * @Methods Name isTheSameDay
     * @Create In Jun 16, 2011 By lee
     * @param startDate
     * @param endDate
     * @return boolean
     */
	 public static boolean isTheSameDay(Date startDate, Date endDate) {
		 SimpleDateFormat myFormatter = new SimpleDateFormat(datePattern);
	       long value = 1;
	         try {
				 Date sdate = myFormatter.parse(getDateStr(startDate));
				 Date edate = myFormatter.parse(getDateStr(endDate));
				 value = (edate.getTime()-sdate.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(value==0){
				return true;
			}
	        return false;
     }
	 
	 public static String getQuartzExpress(Date date){
         SimpleDateFormat sdf= new SimpleDateFormat("s m H d M ? yyyy"); 
         return sdf.format(date);
	 }
	 
	 public static String getQuartzExpress(String timeStr) {
		return getQuartzExpress(DateUtils.convertStringToDate(timeStr));
	 }

	 /**
	  * 获取当前日期所属周对应的（年+第几周）的int数字，如201601
	  * 比如2016-12-26到2017-01-01，属于2016年的最后一周返回201652，2017-01-02为2017年第一周的开始。
	  * @param date
	  * @return
	  * @author lee
	  * @date 2016年10月20日
	  */
	 public static int getYearWeek(Date date) {
		 Calendar c = new GregorianCalendar();
		 c.setFirstDayOfWeek(Calendar.MONDAY);
		 c.setMinimalDaysInFirstWeek(7);
		 c.setTime(date);
		 return c.getWeekYear() * 100 + c.get(Calendar.WEEK_OF_YEAR);
	 }
	 /**
	  * 获取当前日期所属周对应的（年+第几周）的int数字，如201601
	  * 比如2016-12-26到2017-01-01，属于2016年的最后一周返回201652，2017-01-02为2017年第一周的开始。
	  * @param date
	  * @return
	  * @author lee
	  * @date 2016年10月20日
	  */
	 public static int getYearMonth(Date date) {
		 SimpleDateFormat df = new SimpleDateFormat(yearMonthPattern);
		 return Integer.valueOf(df.format(date));
	 }
	 /**
	  * 获取当前日期所属周对应的（年+第几天）的int数字，如2017001
	  * @param date
	  * @return
	  * @date 2016年10月20日
	  */
	 public static int getYearDay(Date date) {
		 Calendar c = new GregorianCalendar();
		 c.setTime(date);
		 int index = c.get(Calendar.DAY_OF_YEAR);
		 String indexStr = String.format("%03d", index);
		 int year = c.get(Calendar.YEAR);
		 return Integer.valueOf(year+""+indexStr);
	 }
	 /**
	  * 获取当前周
	  * @return
	  * @author lee
	  * @date 2016年6月24日
	  */
	 public static int getWeekOfNow() {
		 return getWeekOfDate(new Date());
		}
	 public static int getWeekOfDate(Date date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			calendar.setTime(date);
			int week = (int) ((calendar.getTimeInMillis()-distance)/WEEK_CYCLE)+1;
			return Integer.parseInt(""+(week<10?("0"+week):week));
		}
	public static int getMonthNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return Integer.parseInt(""+year+(month<10?("0"+month):month));
	}
	public static int getMonthInt() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH)+1;
		return month;
	}
	public static int getMonthOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year*100+month;
	}
	public static int getMonthOfNow(){
		Date now = new Date();
		return getMonthOfDate(now);
	}
	public static int getYearInt() {
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	 /**
	  * 获取今天开始时间
	  * @return
	  * @author lee
	  * @date 2016年7月1日
	  */
	public static Long getTodayBeginTime() {
		String today = DateUtils.getCurrentDateStr() + " 00:00:00";
		Date todayBegin = DateUtils.convertStringToDate(today);
		return todayBegin.getTime();
	}
	
	/**
	 * 获取今天结束时间
	 * @return
	 * @author lee
	 * @date 2016年7月1日
	 */
	public static Long getTodayEndTime() {
		String today = DateUtils.getCurrentDateStr() + " 00:00:00";
		Date todayBegin = DateUtils.convertStringToDate(today);
		return add(todayBegin, 1, Calendar.DAY_OF_YEAR).getTime();
	}
	/**
	 * 转换日期 
	 * @param timeMillis
	 * @param format
	 * @return
	 */
	public static String format(Long timeMillis, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(timeMillis);
	}
	/**
	 * 将日期转换为long型
	 * 
	 * @return
	 */
	public static long formatLong(String recDateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		long formatlong = 0;
		try {
			Date date = sdf.parse(recDateStr);
			formatlong = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return formatlong;
	}
	/**
	 * 获得参数时间小时的开始和结束时间
	 * @param date
	 * @return
	 */
	public static long[] getHourBeginAndEndTime(Date date) {
		long[] beginAndEndTime = new long[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		beginAndEndTime[0] = calendar.getTimeInMillis();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		beginAndEndTime[1] = calendar.getTimeInMillis();
		return beginAndEndTime;
	}
	
	/**
	 * 得到参数时间一天的开始和结束时间
	 * @param date
	 * @return
	 * @author lee
	 * @date 2016年7月19日
	 */
	public static long[] getDayBeginAndEndTime(Date date) {
		long[] beginAndEndTime = new long[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		beginAndEndTime[0] = calendar.getTimeInMillis();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		beginAndEndTime[1] = calendar.getTimeInMillis();
		return beginAndEndTime;
	}

	/**
	 * 得到参数时间一天的开始和结束时间
	 * @param date
	 * @return
	 * @author lee
	 * @date 2016年7月19日
	 */
	public static long getDayEndTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 得到参数时间一天的开始和结束时间
	 * @param date
	 * @return
	 * @author lee
	 * @date 2016年7月19日
	 */
	public static long getDayEndTime(long date) {
		Date now = new Date(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 得到参数时间一天的开始和结束时间
	 * @param date
	 * @return
	 * @author lee
	 * @date 2016年7月19日
	 */
	public static long[] getLastDayBeginAndEndTime(Date date) {
		long[] beginAndEndTime = new long[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		beginAndEndTime[0] = calendar.getTimeInMillis();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		beginAndEndTime[1] = calendar.getTimeInMillis();
		return beginAndEndTime;
	}
	/**
	 * 得到参数时间所在月的开始和结束时间戳
	 * @param date
	 * @return
	 */
	public static long[] getMonthBeginAndEndTime(Date date) {
		long[] beginAndEndTime = new long[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);	//目标月份第一天
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		beginAndEndTime[0] = calendar.getTimeInMillis();
		calendar.add(Calendar.MONTH, 1);
		beginAndEndTime[1] = calendar.getTimeInMillis();
		return beginAndEndTime;
	}
	
	/**
	 * 获取当天开始时间的毫秒值,比如：2015年5月15日0时0分0秒
	 * 
	 * @Title: getTodayStartTime @Description: 
	 * 2015-5-15 上午11:09:55 @param @return @return long @throws
	 */
	public static long getTodayStartTime() {
		try {
			Calendar ca = Calendar.getInstance();
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			ca.set(Calendar.MILLISECOND, 0);
			long time = ca.getTime().getTime() / 1000 * 1000;
			return time;
		} catch (Exception e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 获取本周开始(周一)时间的毫秒值,比如今天是2016-07-22日（周5），返回值则是2016-07-18（周1） 00:00:00的毫秒值
	 * @Methods Name getWeekStartTime
	 * @Create In 2016年7月22日 By liaogs
	 * @return long
	 */
	public static long getWeekStartTime() {
		try {
			Calendar currentDate = Calendar.getInstance();  
			currentDate.setFirstDayOfWeek(Calendar.MONDAY);  
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
			currentDate.set(Calendar.MILLISECOND, 0);
			currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
			return currentDate.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 获取本月开始时间的毫秒值,比如今天是2016-07-22日，返回值则是2016-07-01 00:00:00的毫秒值
	 * @Methods Name getWeekStartTime
	 * @Create In 2016年7月22日 By liaogs
	 * @return long
	 */
	public static long getMonthStartTime() {
		try {
			Calendar currentDate = Calendar.getInstance();  
			// 设置日期为当前年-月-1日的0时0分0秒
			currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),1,0,0,0);
			return currentDate.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 获取本月结束时间的毫秒值,比如今天是2016-07-22日，返回值则是2016-07-31 23:59:59的毫秒值
	 * @Methods Name getWeekStartTime
	 * @Create In 2016年7月22日 By liaogs
	 * @return long
	 */
	public static long getMonthEndTime() {
		try {
			Calendar currentDate = Calendar.getInstance();  
			// 设置日期为当前年-月-1日的0时0分0秒
			currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),1,0,0,0);
			currentDate.add(Calendar.MONTH, 1);
			return currentDate.getTime().getTime()-1;
		} catch (Exception e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}
	
	/**
	 * 获取年月日日期，例如:20150922
	 * @param timeMillis
	 * @param format
	 * @return
	 */
	public static String getDateNum(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		String datestr = sdf.format(time);
		datestr = datestr.replace("-", "");
		return datestr;
	}
	
	public static int getDateInt(long time){
		return Integer.parseInt(getDateNum(time));
	}
	
	/** 
     * 根据年 月 获取对应的月份 天数 
     * */  
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }
    
    /**
	 * 获取 指定日期是周几
	 * 
	 * @author weizhensong
	 * @param date 形如2016-08-29
	 * @return
	 */
	public static int getWeekDay(String dateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			Date date = sdf.parse(dateStr);
			int[] weekDays = {0, 1, 2, 3, 4, 5, 6};
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (w < 0)
				w = 0;
			return weekDays[w];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 两个日期相差多少小时
	 * @Methods Name getDifferenceBetweenHour
	 * @Create In 2016年9月11日 By liaogs
	 * @param beforTime：较前的一个日期毫秒数
	 * @param afterTime：较后的一个日期毫秒数
	 * @return int：相差的天数
	 */
	public static int getDifferenceBetweenHour(Long beforTime,Long afterTime){
		if(null != beforTime && null != afterTime){
			Long betweenDays = Math.abs((afterTime-beforTime)) / (1000*3600);
			return Integer.parseInt(String.valueOf(betweenDays));
		}
		return 0;
	}
	
	public static Long getYYYYMMDDHHMM(long targetTimeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat(longminutePattern);
		String timestr = sdf.format(targetTimeMillis);
		return Long.valueOf(timestr);
	}
	
	/**
	 * 获取上个月的年月信息
	 * @Methods Name getMonthOfLastMonthDate
	 * @Create In 2017年4月17日 By liaogs
	 * @return int
	 */
	public static int getMonthOfLastMonthDate() {
		// 上个月的数据
		Date date = new Date();
		Date targetDate = DateUtils.addMonths(date, -1);
		return DateUtils.getMonthOfDate(targetDate);
	}
	
	/**
	 * 根据日期获取上个月的年月信息
	 * @Methods Name getMonthOfLastMonthDate
	 * @Create In 2017年4月17日 By liaogs
	 * @param date
	 * @return int
	 */
	public static String getMonthOfLastMonthDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String time = format.format(calendar.getTime());
		return time;
	}
	
	/**
	 * 根据日期获取上个月的时间
	 * @Methods Name getLastMonthDate
	 * @Create In 2017年4月17日 By liaogs
	 * @param date
	 * @return int
	 */
	public static Date getLastMonthDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}
	
	public static Date getLastMonthDate(Date date,int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -num);
		return calendar.getTime();
	}
	
	/**
	 * 根据整形yyyyMM转换成字符型yyyy-MM
	 * @Methods Name getStrDate
	 * @Create In 2017年4月17日 By liaogs
	 * @param tempDate
	 * @return String
	 */
	public static String getStrDate(Integer tempDate){
		String dateStr = "";
		if(tempDate > 180000){
			int year = tempDate / 100;
			int month = tempDate % 100;
			if(month < 10){
				dateStr = year + "-0" + month;
			}else{
				dateStr = year + "-" + month;
			}
		}
		return dateStr;
	}
	
	public static int getTotalDayByDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(date);
		//Calendar对象默认一月为0,month月
		int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
		return day;
	}
	public static long getLastOneHalfHour(){
    	Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 40);
		return calendar.getTime().getTime();
    }
	
	public static long getLastTargetTime(int hour,int minite){
    	Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minite);
		return calendar.getTime().getTime();
    }
	public static long getLastDayOfYearTime(){
    	Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
		return calendar.getTime().getTime();
    }
	/**
	 * 得到过去时间 n 小时的时间
	 * @param date
	 * @param hour
	 * @return
	 */
	public static long[] getLastHourBeginAndEndTime(Date date,int hour) {
		long[] beginAndEndTime = new long[2];
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    beginAndEndTime[1] = cal.getTime().getTime();
	    cal.add(Calendar.HOUR_OF_DAY, hour);
	    beginAndEndTime[0] = cal.getTime().getTime();
		return beginAndEndTime;
	}

	public static long[] getWeekBeginAndEndTime(Date date) {
		long[] beginAndEndTime = new long[2];
		try {
			Calendar currentDate = Calendar.getInstance(); 
			currentDate.setTime(date);
			currentDate.setFirstDayOfWeek(Calendar.MONDAY);  
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
			currentDate.set(Calendar.MILLISECOND, 0);
			currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  
			beginAndEndTime[0] = currentDate.getTime().getTime();
			currentDate.set(Calendar.DAY_OF_WEEK, 8);
			currentDate.set(Calendar.HOUR_OF_DAY, 23);  
			currentDate.set(Calendar.MINUTE, 59);  
			currentDate.set(Calendar.SECOND, 59);
			currentDate.set(Calendar.MILLISECOND, 999);
			beginAndEndTime[1] = currentDate.getTime().getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beginAndEndTime;
	}
	public static long getWeekEndTime(Date date) {
		Calendar currentDate = Calendar.getInstance(); 
		try {
			currentDate.setTime(date);
			currentDate.setFirstDayOfWeek(Calendar.MONDAY);
			currentDate.set(Calendar.DAY_OF_WEEK, 8);
			currentDate.set(Calendar.HOUR_OF_DAY, 23);  
			currentDate.set(Calendar.MINUTE, 59);  
			currentDate.set(Calendar.SECOND, 59);
			currentDate.set(Calendar.MILLISECOND, 999);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentDate.getTime().getTime();
	}
	/**
	 * @Description: 是否是本月第一天
	 * @param date
	 * @return   
	 * boolean
	 */
	public static boolean isCurrFirstDay() {
		Calendar c = Calendar.getInstance();
		// 得到本月的那一天
		int today = c.get(Calendar.DAY_OF_MONTH);
		// 然后判断是不是本月的第一天
		if(today ==1){
			return true; //是
		}else{
			return false; //否
		}
	}
	
	public static long getBeforeTwoMonthTime() {
		// 获取最近二月，含当月
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		// 获得当前月第一天
		Date sdate = calendar.getTime();
		long beforeTwoMonthTime = DateUtils.addMonths(sdate, -2).getTime();
		return beforeTwoMonthTime;
	}
	
	/**
	 * @Description: 时间戳转Date
	 * @param time
	 * @return   
	 * Date
	 */
	public static Date convertTimeToDate(Long time){
		 SimpleDateFormat format = new SimpleDateFormat(timePattern);
		 String d = format.format(time);
		 try {
			return format.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @Description: 相差天数
	 * @param date1
	 * @param date2
	 * @return int
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}
	
	/**
	 * @Description: 相差天数2
	 * @param date1
	 * @param date2
	 * @return int
	 */
	public static int differentDaysByMillisecond(Long beginTime, Long endTime) {
		int days = (int) ((endTime - beginTime) / (1000 * 3600 * 24));
		return days;
	}
	
	/**
	 * @Description: 根据两个时间戳月份,来查询有几个月(包含俩个时间戳自身的月份) 比如：2017-11 和 2018-1之间3个月
	 * @param beginTime
	 * @param endTime
	 * @return int
	 * @throws ParseException 
	 */
	public static int getMonthNumBeginAndEndTime(Long beginTime,Long endTime) {
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(new Date(beginTime));
        end.setTime(new Date(endTime));
        int result = end.get(Calendar.MONTH) - begin.get(Calendar.MONTH);
        int month = (end.get(Calendar.YEAR) - begin.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result) + 1;  
	}
	
	public static Long getNear26Day() {
		Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -26);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        return now.getTimeInMillis();
	}
	
	public static Long getNearDay(int day) {
		Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, day);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        return now.getTimeInMillis();
	}
	public static Long getNextYear(Long day) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date(day));
		now.set(Calendar.YEAR, now.get(Calendar.YEAR)+1);
        return now.getTimeInMillis();
	}
	/*public static void main(String[] args) throws Exception {
		String originalImg = "d:test/111.jpg";
		// 读入大图
		File file = new File(originalImg);
		FileInputStream fis = new FileInputStream(file);
		BufferedImage image = ImageIO.read(fis);

		// 分割成4*3(12)个小图
		int rows = 4;
		int cols = 3;
		int chunks = rows * cols;

		// 计算每个小图的宽度和高度
		int chunkWidth = image.getWidth() / cols;
		int chunkHeight = image.getHeight() / rows;

		int count = 0;
		BufferedImage imgs[] = new BufferedImage[chunks];
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				// 设置小图的大小和类型
				imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

				// 写入图像内容
				Graphics2D gr = imgs[count++].createGraphics();
				gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
						chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
				gr.dispose();
			}
		}

		// 输出小图
		for (int i = 0; i < imgs.length; i++) {
			ImageIO.write(imgs[i], "jpg", new File("d:test/" + i + ".jpg"));
		}

		System.out.println("完成分割！");
	}*/
	
	public static Integer getHourByTarget(Long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		return Integer.valueOf(sdf.format(time));
	}
	public static void main(String[] args) {

		/*System.out.println(DateUtils.DAY*7);
		Integer startTime=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date(DateUtils.getNearDay(-50))));
		System.out.println(startTime);
		Integer endTime=Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date(DateUtils.getTodayBeginTime())));
		System.out.println(endTime);*/

        Date lastMonthDate = null;
        try {
            lastMonthDate = DateUtils.getLastMonthDate(DateUtils.addMonths(new SimpleDateFormat("yyyyMM").parse("201811"), 1));
            long[] lastMonth = DateUtils.getMonthBeginAndEndTime(lastMonthDate); //获取上月开始时间和本月开始时间
            System.out.println(lastMonthDate);
            System.out.println(Arrays.toString(lastMonth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
}
