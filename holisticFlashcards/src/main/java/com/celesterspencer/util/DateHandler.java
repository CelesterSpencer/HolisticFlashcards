package com.celesterspencer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHandler {

	Date m__date;
	String m__dateRepresentation;
	
	//-------------------------------------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------------------------------------
	/**
	 * Constructor initializes with todays date
	 */
	public DateHandler() {
		m__date = new Date();
		m__dateRepresentation = dateToString(m__date);
	}
	
	public DateHandler(String dateRepresentation) {
		m__dateRepresentation = dateRepresentation;
		m__date = stringToDate(dateRepresentation);
	}
	
	public DateHandler(Date date) {
		m__date = date;
		m__dateRepresentation = dateToString(date);
	}


	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC GETTER AND SETTER
	//-------------------------------------------------------------------------------------------------------------------	
	public Date getDate () {
		return m__date;
	}
	
	public String getStringRepresentation() {
		return m__dateRepresentation;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PUBLIC METHODS
	//-------------------------------------------------------------------------------------------------------------------	
	public void incrementBy(int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(m__date);
        c.add(Calendar.DATE, days);  
        m__date = new Date(c.getTimeInMillis());
        m__dateRepresentation = dateToString(m__date);
	}
	
	/**
	 * Compares this date to dateToCompare
	 * @param dateToCompare
	 * @return 0 if dates are equal by the day, <0 if this date is before the dateToCompare, >0 if this date is after the dateToCompare 
	 */
	public int compareTo(Date dateToCompare) {
        int compare = m__date.compareTo(dateToCompare);
        return compare;
	}
	
	/**
	 * Compares this date to dateToCompare. Date must be "yyyy-MM-dd" formated
	 * @param dateToCompare
	 * @return 0 if dates are equal by the day, <0 if this date is before the dateToCompare, >0 if this date is after the dateToCompare 
	 */
	public int compareTo(String dateToCompare) {
		Date tempDate = stringToDate(dateToCompare);
        int compare = m__date.compareTo(tempDate);
        return compare;
	}
	
	public long distanceTo(Date dateToCompare) {
        long distance = Math.abs(m__date.getTime() - dateToCompare.getTime());
        return distance;
	}
	
	public long distanceTo(String dateToCompare) {
		Date tempDate = stringToDate(dateToCompare);
        long distance = Math.abs(m__date.getTime() - tempDate.getTime());
        return distance;
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------
	// PRIVATE METHODS
	//-------------------------------------------------------------------------------------------------------------------
	private Date stringToDate(String dateRepresentation) {
		Date date;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateRepresentation);
		} catch (ParseException e) {
			Logger.log("Date cannot be parsed", "ERROR");
			date = new Date();
			e.printStackTrace();
		}
        return date;
	}
	
	private String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateRepresentation = sdf.format(date);
		return dateRepresentation;
	}
	
}
