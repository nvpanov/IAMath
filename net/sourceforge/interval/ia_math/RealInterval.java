
package net.sourceforge.interval.ia_math;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import net.sourceforge.interval.ia_math.exceptions.*;
import static java.lang.Math.min;
import static java.lang.Math.max;


/**
 *
 * RealInterval.java <p>
 *  -- classes implementing real intervals
 *     as part of the "ia_math library" version 0.1beta1, 10/97
 *
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class RealInterval represents closed intervals of real numbers
 */

public class RealInterval implements Cloneable{

	// nvpanov: this is really essential for stability.
	// IAMath contains number of bugs, including changing 
	// interval values in some functions
	// so it was decided to make intervals UNMUTABLE
	// UPDATE. Actually it is MUTABLE now. The only
	// function so far supposed to change it's value is
	// intersect()
	// no any kind of other setters supposed to be 
	/*final*/ double lo;
	/*final*/ double hi;

	private static final DecimalFormat plainFormat2digits;
	private static final DecimalFormat scientificFormat3digits;

	static {
		  Locale L = new Locale("en");
		  Locale.setDefault(L);	  
		  plainFormat2digits = new DecimalFormat("#0.00");
		  DecimalFormatSymbols s = plainFormat2digits.getDecimalFormatSymbols();
		  s.setInfinity("inf");
		  s.setDecimalSeparator('.');
		  s.setGroupingSeparator(',');	  
		  plainFormat2digits.setDecimalFormatSymbols(s);
		  plainFormat2digits.setGroupingSize(3);
		  plainFormat2digits.setGroupingUsed(true);
		  
		  scientificFormat3digits = new DecimalFormat("0.000E0");
		  DecimalFormatSymbols s2 = scientificFormat3digits.getDecimalFormatSymbols();
		  s2.setInfinity("inf");
		  s2.setDecimalSeparator('.');
		  scientificFormat3digits.setDecimalFormatSymbols(s2);
	}
  public RealInterval(double lo, double hi) throws IARuntimeException
  {
//	  isEmpty = false;
    if (lo <= hi) {
      this.lo = lo; this.hi = hi;
    }
    else throw new IARuntimeException("RealInterval(x="+lo+",y="+hi+"): must have x<=y");
  }

  public RealInterval(double x) throws IARuntimeException {
    if (Double.isInfinite(x) || Double.isNaN(x) )
    		throw new IllegalArgumentException("RealInterval(x=" + x + "): must have not a NaN and -inf<x<inf");
    lo = x; 
    hi = x;
  }

  /**
   * construct the interval [-infty,infty]
   */
  public RealInterval() {
//	  isEmpty = false;
	  this.lo = java.lang.Double.NEGATIVE_INFINITY;
	  this.hi = java.lang.Double.POSITIVE_INFINITY;
  }

  public RealInterval(RealInterval x) {
	// copy-constructor
	    lo = x.lo; 
	    hi = x.hi;
  }

public double lo() {
    return this.lo;
  }

  public double hi() {
    return this.hi;
  }

	// as far as we implementing custom equals
	// we need to implement hashCode as well
	@Override
	public int hashCode() {
		// not very fast implementation
		// but simple. 
		// Anyway it uses the same fields as
		// equals() do, so it is correct.
		//return toString().hashCode();
		int hash = 37;
		hash = hash*17 + Double.valueOf(lo).hashCode();
		hash = hash*17 + Double.valueOf(hi).hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object thata) {
	    //check for self-comparison
	    if ( this == thata ) return true;

	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explicit check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    if ( !(thata instanceof RealInterval) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    RealInterval that = (RealInterval)thata;

	    //now a proper field-by-field evaluation can be made
	    return equals(that);
	}
	private static boolean compareInfinitys(double a, double b) {
		if ( 	Double.isInfinite(a) && Double.isInfinite(b) 
					&&
				(a > 0 && b > 0) || (a < 0 && b < 0) 
			) {
				return true;
		} else
			return false;
	}
	public boolean equals(RealInterval x) { 
		if ( this.lo() == x.lo() ) {
			if ( this.hi() == x.hi() )
				return true;
			else return compareInfinitys(this.hi(), x.hi());
		}
		return compareInfinitys(this.lo(), x.lo()) && this.hi() == x.hi();
    }
    		
	@Override
	public String toString() {
		return toString12();
	}
  private String toString11(){
	  return "[" + plainFormat2digits.format(lo) + ", " + plainFormat2digits.format(hi) + "]";
  }  
  private String toString12(){
	  return "[" + scientificFormat3digits.format(lo) + ", " + scientificFormat3digits.format(hi) + "]";
  }  
  private String toString1(){
    return
        "[" +
         doubleToString(this.lo) +
        " , " +
         doubleToString(this.hi) +
        "]";
    }

  private String toString1a(){
    return
        "[" +
        ((new Double(this.lo)).toString()) +
        " , " +
        ((new Double(this.hi)).toString()) +
        "]";
    }

  private String toString2(){
    Double midpoint =  new Double((this.lo + this.hi)/2.0);
    String midpointString =  doubleToString((this.lo + this.hi)/2.0);
    String      hi1String =  doubleToString(this.hi - midpoint.doubleValue());
    if (Math.abs(midpoint.doubleValue()) > (this.hi-this.lo)/2.0)
      return
        "("+
        midpointString +
        " +/- " +
        hi1String +
        ") ";
    else
     return(this.toString1());

  }


  private String doubleToString(double x) {
    StringBuffer s = new StringBuffer((new Double(x)).toString());
    int i = s.length(); 
    int j;
    for(j=1;j<20-i;i++) s.append(' ');
    return(s.toString());
  }

  public Object clone() {
    return new RealInterval(this.lo,this.hi);
  }
  
  // nvpanov
  public double wid() {
	  return Math.abs(hi - lo);
  }
	  //nvpanov
	  public boolean contains(double value) {
		  if (!Double.isInfinite(value))
			  return this.lo() <= value && value <= this.hi();
		  else
			  return compareInfinitys(lo(), value) || compareInfinitys(hi(), value);
	  }
  public boolean contains(RealInterval x) {
	  if (x == null)
		  return false;
	  return contains(x.hi()) && contains(x.lo());
  }  
  public boolean containsNotEqual(RealInterval x) {
	  return contains(x) && !equals(x);
  }

  public boolean isIntersects(RealInterval y) {
		RealInterval x = this;
		return (x.contains(y.lo()) || x.contains(y.hi()) ||
				y.contains(x.lo()) || y.contains(x.hi()) );
	}

  
  
  
public boolean almostEquals(RealInterval i) {
	return Math.abs( (lo - i.lo) + (hi - i.hi) ) < 1e-3;
}

public static RealInterval valueOf(String value) {
	if (value == null)
		throw new NumberFormatException("Null as a string to create Interval");
	value = value.trim();
	if (value.isEmpty())
		throw new NumberFormatException("Empty string to create Interval");
	if (value.length() > 100)
		throw new NumberFormatException("So long string to create an interval. Current limit is 100 chars.");
	
	double dVal;
	try { 
		dVal = Double.valueOf(value);
		return new RealInterval(dVal);
	} catch (NumberFormatException e) {
		//this is not a single number -- continue
	}
	final String message = "String '" + value + "' is wrong to create an interval. " +
			"It should be a single double or contains two doubles separated by " +
			"',' or ';' and enclosed in square breakets: '[...; ...] (1)";
	
	if (value.charAt(0) != '[' && value.charAt(value.length()-1) != ']')
		throw new NumberFormatException(message);
	value = value.substring(1, value.length()-1);
	RealInterval res = createIntervalByTwoNumbers(value, ";");	
	if (res == null)
		res = createIntervalByTwoNumbers(value, ",");
	if (res == null)	
		throw new NumberFormatException(message);
	return res;
}
	private static RealInterval createIntervalByTwoNumbers(String string, String delimiter) {
		final String message = "String '" + string + "' is wrong to create an interval. " +
				"It should be a single double or contains two doubles separated by " +
				"',' or ';' and enclosed in square breakets: '[...; ...]";
		String borders[] = string.split(delimiter);
		if (borders.length == 2) {
			try { 
				double lo = Double.valueOf(borders[0]);
				double hi = Double.valueOf(borders[1]);
				return new RealInterval(lo, hi);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(message);
			}
		}
		if (borders.length > 2) 
			throw new NumberFormatException(message);		
		return null;
	}

}
