package net.sourceforge.interval.ia_math;

import static java.lang.Math.min;
import static java.lang.Math.max;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import net.sourceforge.interval.ia_math.exceptions.IAComputationalException;
//import net.sourceforge.interval.ia_math.exceptions.IAIntersectionException;
import static net.sourceforge.interval.ia_math.IMathDetails.*;

/**
 * IAMath.java 
 *   -- classes implementing interval arithmetic versions
 *      of the arithmetic and elementary functions,
 *      as part of the "ia_math library" version 0.1beta1, 10/97
 * 
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class IAMath contains methods for performing basic
 * arithmetic operations on intervals. Currently the
 * elementary functions rely on the underlying implementation
 * which uses the netlib fdlibm library. The resulting code
 * is therefore probably unsound for the transcendental functions.
 */

public class IAMath
{
	// nvp
	public static void useSimpleRounding(boolean simple) {
		RMath.simpleRounding = simple;
	    System.out.println("IAMath simpleRounding:" + simple);
	}
	public static double wid(RealInterval i) {
	    	return Math.abs(i.hi() - i.lo());
	 }
	  public static RealInterval intersect(RealInterval a, RealInterval b) {
		  if (a == null || b == null ||
				  !a.isIntersects(b)) 
		  {
			  //throw new IAIntersectionException(this + " and " + with + " doesn't overlap");
			  return null;
		  }
		  double l = max( a.lo, b.lo );
		  double h = min( a.hi, b.hi );	  	  
		  return new RealInterval(l, h);
	  }
	  public static RealInterval intersect(RealInterval[] ii) {
		  RealInterval intersection = ii[0];
		  for (int i = 1; i < ii.length; i++) {
			  intersection = intersect(intersection, ii[i]);
		  }
		  return intersection;
	  }
	 
	
  public static RealInterval add(RealInterval x, RealInterval y) {
	  double l = RMath.add_lo(x.lo,y.lo);
	  double h = RMath.add_hi(x.hi,y.hi);
	  return new RealInterval(l, h);
  }

  public static RealInterval add(RealInterval i, double d) {
	  return new RealInterval(i.lo + d, i.hi + d);
  }
 
  public static RealInterval sub(RealInterval x, RealInterval y) {
    double l = RMath.sub_lo(x.lo,y.hi);
    double h = RMath.sub_hi(x.hi,y.lo);
    return new RealInterval(l, h);
  }
  public static RealInterval sub(RealInterval x, double y) {
    return new RealInterval(x.lo - y, x.hi - y);
  }
  public static RealInterval sub(double x, RealInterval y) {
	    return new RealInterval(x - y.hi, x - y.lo);
  }
  //nvp 
  public static RealInterval negate(RealInterval x) {
	  return new RealInterval(-x.hi(), -x.lo() );
  }
  

  // nvp
  public static RealInterval mul(RealInterval y, double x) {
	  return mul(x, y);
  }
  // nvp
  public static RealInterval mul(double x, RealInterval y) {
	   double yl = y.lo();
	   double yu = y.hi();
	   if (x < 0)
		   return new RealInterval(RMath.mul_lo(x, yu), RMath.mul_hi(x, yl) );
	   if (x > 0)
		   return new RealInterval(RMath.mul_lo(x, yl), RMath.mul_hi(x, yu) ); 
	   return zero;
  }
  
  public static RealInterval mul(RealInterval x, RealInterval y) {

	  double xl = x.lo();
    double xu = x.hi();
    double yl = y.lo();
    double yu = y.hi();
    double l, h;
    
    if (xl < 0)
    	if (xu > 0)
    		if (yl < 0)
    			if (yu > 0) { // M * M
    				l = min( RMath.mul_lo(xl, yu), RMath.mul_lo(xu, yl) ); 
    				h = max( RMath.mul_hi(xl, yl), RMath.mul_lo(xu, yu) );
    				return new RealInterval(l, h);
    			}
    			else  		// M * N
    				return new RealInterval(RMath.mul_lo(xu, yl), RMath.mul_hi(xl, yl) );
    		else
    			if (yu > 0) // M * P
    				return new RealInterval(RMath.mul_lo(xl, yu), RMath.mul_hi(xu, yu) );
    			else		// M * Z
    				return zero;
    	else
    		if (yl < 0)
    			if (yu > 0) // N * M
    				return new RealInterval(RMath.mul_lo(xl, yu), RMath.mul_hi(xl, yl) );
    			else 		// N * N
    				return new RealInterval(RMath.mul_lo(xu, yu), RMath.mul_hi(xl, yl) );
    		else 
    			if (yu > 0) // N * P
    				return new RealInterval(RMath.mul_lo(xl, yu), RMath.mul_hi(xu, yl) );
    			else 		// N * Z
    				return zero;
    else 
    	if (xu > 0)
    		if (yl < 0)
    			if (yu > 0) // P * M
    				return new RealInterval(RMath.mul_lo(xu, yl), RMath.mul_hi(xu, yu) );
    			else 		// P * N
    				return new RealInterval(RMath.mul_lo(xu, yl), RMath.mul_hi(xl, yu) );
    		else 
    			if (yu > 0)	// P * P 
    				return new RealInterval(RMath.mul_lo(xl, yl), RMath.mul_hi(xu, yu) );
    			else 		// P * Z
    				return zero;
    	else 				// Z * ?
    		return zero;
  }


  public static RealInterval div(RealInterval x, RealInterval y) throws IAComputationalException {
	  if (x.lo() == 0 && x.hi() == 0)
		  return zero;
	  if (y.contains(0)) {
		  if (y.lo() != 0)
			  if (y.hi() != 0) // x / [y .. 0 .. y]
				  return div_zero(x);
			  else  // x / [y, 0]
				  return div_negative(x, y.lo());
		  else
			  if (y.hi() != 0) // x / [0, y] 
				  return div_positive(x, y.hi());
			  else // x/ [0, 0]
				  throw new IAComputationalException("Division by Zero");
	  }
	  return div_non_zero(x, y);
  }




  /**
   * this performs (y := y intersect z/x) and succeeds if
   * y is nonempty.
   */
/*	
  public static boolean intersect_odiv(
       RealInterval y,RealInterval z,RealInterval x)
    throws IAException
  {
    if ((x.lo >= 0) || (x.hi <= 0)) {
      y.intersect(IAMath.odiv(z,x));
      return true;
    }else
    if (z.lo >0) {
      double tmp_neg = RMath.div_hi(z.lo,x.lo);
      double tmp_pos = RMath.div_lo(z.lo,x.hi);
      if (   ((y.lo > tmp_neg) || (y.lo == 0))
          && (y.lo < tmp_pos)) y.lo = tmp_pos;
      if (   ((y.hi < tmp_pos) || (y.hi == 0))
          && (y.hi > tmp_neg)) y.hi = tmp_neg;
      if (y.lo <= y.hi) return true;
      else throw new IAException("intersect_odiv(Y,Z,X): intersection is an Empty Interval");
    }
    else if (z.hi < 0) {
      double tmp_neg = RMath.div_hi(z.hi,x.hi);
      double tmp_pos = RMath.div_lo(z.hi,x.lo);
      if (   ((y.lo > tmp_neg) || (y.lo == 0))
          && (y.lo < tmp_pos)) y.lo = tmp_pos;
      if (   ((y.hi < tmp_pos) || (y.hi == 0))
          && (y.hi > tmp_neg)) y.hi = tmp_neg;
      if (y.lo <= y.hi) return true;
      else throw new IAException("intersect_odiv(Y,Z,X): intersection is an Empty Interval");
    }
    else return(true);
  }
*/

  
// should be moved to RealInterval? 
	  public static RealInterval union(RealInterval x, RealInterval y) //throws IAIntersectionException
	  {
		  if (x.isIntersects(y))
		  	return new RealInterval(min(x.lo(), y.lo()), max(x.hi(), y.hi()));
		  //throw new IAIntersectionException(x + " and " + y + " doesn't overlap");
		  return null;
	  }
// moved to RealInterval
/*	  
	  public static RealInterval intersection(RealInterval x, RealInterval y) throws IAIntersectionException {
		  if (x.isIntersects(y))
			  return new RealInterval(max(x.lo(),y.lo()), min(x.hi(),y.hi()));
		  throw new IAIntersectionException(x + " and " + y + " doesn't overlap");	  
	  }
*/


  public static RealInterval uminus(RealInterval x) {
	  return new RealInterval(-x.hi, -x.lo);
  }
  
  

  public static RealInterval exp(RealInterval x) {
    return new RealInterval(RMath.exp_lo(x.lo()), RMath.exp_hi(x.hi()));
  }

  public static RealInterval log(RealInterval x) throws IAComputationalException {
    if (x.hi <= 0) 
      throw new IAComputationalException("Negative value for log(x=" + x + ")");

    double l = x.lo() > 0 ? RMath.log_lo(x.lo) : Double.NEGATIVE_INFINITY; 
    return new RealInterval(l, RMath.log_hi(x.hi));
  }
  public static RealInterval ln(RealInterval x) throws IAComputationalException {
	  return log(x);
  }

  public static RealInterval sin(final RealInterval x) {
	  RealInterval y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
	  RealInterval z = sin2pi(y);
	  return z;
  }

  public static RealInterval cos(final RealInterval x) {
    RealInterval y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
    RealInterval z = cos2pi(y);
    return(z);
  }

  
  public static RealInterval tan(RealInterval x) {
	  RealInterval y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
	  RealInterval z = tan2pi(y);
	  return(z);
  }
  public static RealInterval tg(RealInterval x) {
	  return tan(x);
  }
  public static RealInterval ctg(RealInterval x) {
	  throw new NotImplementedException();
  }

	public static RealInterval asin(RealInterval x) {
		RealInterval area = new RealInterval(x);
		area = intersect(area, one_one);
		if (area == null)
			throw new IllegalArgumentException();
		RealInterval z = 
			new RealInterval(RMath.asin_lo(area.lo()), RMath.asin_hi(area.hi()));
		return z;
		
	}

	public static RealInterval acos(RealInterval x) {
		return new RealInterval(RMath.acos_lo(x.hi), RMath.acos_hi(x.lo));
	}

	public static RealInterval atan(RealInterval x) {
		return new RealInterval(RMath.atan_lo(x.hi), RMath.atan_hi(x.lo));
	}

	public static RealInterval arctg(RealInterval r) {
		throw new NotImplementedException();
	}
	public static RealInterval arcctg(RealInterval x) {
		return sub(Math.PI/2, arctan(x) );
	}
	public static RealInterval arctan(RealInterval x) {
		throw new NotImplementedException();
		//http://upload.wikimedia.org/math/e/9/c/e9c693957255f9968d3c4af39a06d0e6.png
	}
	public static RealInterval arccos(RealInterval r) {
		throw new NotImplementedException();
	}
	public static RealInterval arcsin(RealInterval r) {
		throw new NotImplementedException();
	}
	







/*
  public static RealInterval midpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = (x.lo + x.hi)/2.0;
    z.hi = z.lo;

    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      return(z);
    }
    else if ((Double.NEGATIVE_INFINITY == x.lo)) {
      if (x.hi > 0.0) {
        z.lo = 0.0; z.hi = z.lo; return(z);
      } else if (x.hi == 0.0){
        z.lo = -1.0; z.hi = z.lo; return(z);
      } else {
        z.lo = x.hi*2; z.hi = z.lo; return(z);
      }
    } else if ((Double.POSITIVE_INFINITY == x.hi)) {
      if (x.lo < 0.0) {
        z.lo = 0.0; z.hi = z.lo; return(z);
      } else if (x.lo == 0.0){
        z.lo = 1.0; z.hi = z.lo; return(z);
      } else {
        z.lo = x.lo*2; z.hi = z.lo; return(z);
      }
    } else {
      z.lo = x.lo; z.hi = x.hi;
      System.out.println("Error in RealInterval.midpoint");
      return(z);
    }
  }


  public static RealInterval leftendpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = x.lo;
    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      z.hi = z.lo;
      return(z);
    }else {
      z.lo = RMath.nextfp(x.lo);
      z.hi = z.lo;
     return(z);
    }
  }


  public static RealInterval rightendpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = x.hi;
    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      z.hi = z.lo;
      return(z);
    }else {
      z.lo = RMath.prevfp(x.hi);
      z.hi = z.lo;
     return(z);
    }
  }
*/

  /**
   *  returns (x**y) computed as exp(y*log(x))
   */
  public static RealInterval power(RealInterval x, RealInterval y)
    throws IllegalArgumentException
 {
	  if (y.hi == 1 && y.hi == y.lo)
		  return x;
	  if (y.hi == 0 && y.hi == y.lo)
		  return new RealInterval(x.contains(0) ? 0 : 1, 1); // 0^1 = 0, x^1 = 1 
	  if (x.hi == 0 && x.hi == x.lo)
		  return new RealInterval(0); // here we know that it is not 0^1 case
	  
      if (x.hi <= 0) {
    	  if (y.lo == y.hi) {
    		  double t1 = Math.pow(x.lo, y.lo);
    		  double t2 = Math.pow(x.hi, y.lo);
    		  double min, max;
    		  if (t1 < t2) {
    			  min = t1;
    			  max = t2;
    		  } else {
    			  min = t2;
    			  max = t1;
    		  }
    		  return new RealInterval(min, max);
    	  }
    	  // due to log(x): x<=0 not allowed
    	  throw new IllegalArgumentException("power(X,Y): X<=0 not allowed");
      }
      if (x.lo() < 0)
    	  throw new IllegalArgumentException("power(X,Y): X contains 0. It is not allowed by power(I, I) function. Did you mean intPow(I, I)?");
      
      RealInterval z = exp(mul(y,log(x)));
      return z;
  }
  
  /*
   * This pow function treats y not as usual interval but as a set of
   * integers. For example, if y = [-2.8, 1.1] it will assume that y
   * contains only -2, -1, 0, 1. So result = x^-2 union x^-1 union x^0 union x^1.
   */
  public static RealInterval intPow(RealInterval x, RealInterval y) {
	  RealInterval res = power(x, y.lo());
	  for (int i = (int)y.lo() + 1; i <= (int)y.hi(); i++) {
		  RealInterval tmp = power(x, i);
		  res = new RealInterval( min(res.lo(), tmp.lo()), max(res.hi(), tmp.hi()) );
	  }
	  return res;
  }
 
  //nvpanov
  public static RealInterval power(RealInterval x, double y) throws IAComputationalException {
	  return pow(x, y);
  }

	  //nvpanov
  public static RealInterval pow(RealInterval x, double y) throws IAComputationalException {
	  if (y%2 == 0)
		  return evenPower(x, y);
	  return oddPower(x, y);
  }

  /**
   * returns (x**1/y) assuming that y is restricted to integer values
   */
  public static RealInterval root(RealInterval x, int y) 
    throws IllegalArgumentException
  {
    if (y % 2 == 0) {
        if (y == 0)
            throw new IllegalArgumentException("root(X,y): y=0 not allowed");
         return evenRoot(x,y);
    }
    return oddRoot(x,y);
  }
  public static RealInterval sqrt(RealInterval x) {
	  return root(x, 2);
  }

  public static RealInterval root(RealInterval x, RealInterval y) {
		return power(x, div(one, y));		
	}

  /**
   * computes 
   * <code> u :=  (u intersect ((x**1/y) union -(x**1/y)))</code>
   * and returns true if u is nonempty
   * Also, assumes that y is a constant integer interval
   */
  /*
  public static boolean intersectIntegerRoot
     (RealInterval x, RealInterval y, RealInterval u)
    throws IAException
 {
    double yy;
    RealInterval tmp;

    //      System.out.println("intersectIntegerRoot u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    if ((y.lo!=y.hi) || 
      (Math.IEEEremainder(y.lo,1.0)!=0.0)) 
      return true; // the conservative answer
      //       throw new IAException("integerRoot(x,y): y must be a constant integer interval [i,i]");

    yy = y.lo;

    if (Math.IEEEremainder(yy,2.0) != 0.0) {
      //             System.out.println("odd case with yy = "+yy);
      //             System.out.println("x^(1/y) = "+oddRoot(x,yy));
       u.intersection(oddRoot(x,yy));
       //             System.out.println("did odd case u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    }
    else {
      //             System.out.println("even case with yy = "+yy);
      //             System.out.println("x^(1/y) = "+evenRoot(x,yy));
       tmp =  evenRoot(x,yy);
       if (u.hi < tmp.lo)
         u.intersection(uminus(tmp));
       else if (-tmp.lo < u.lo )
         u.intersection(tmp);
       else 
         u.intersection(new RealInterval(-tmp.hi,tmp.hi));

       //              System.out.println("did even case u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    }

    return true;
 }
*/
    



  public static void main(String argv[]) {

     RealInterval a,b,c;

     a = new RealInterval(5.0);
     b = log(a);
     c = exp(b);

     System.out.println("a= "+a);
     System.out.println("log(a)= "+b);
     System.out.println("exp(log(a))= "+c);

    try {
     a = new RealInterval(-5.0,0.0);
     c = exp(log(a));

     System.out.println("a= "+a);
     System.out.println("exp(log(a))= "+c);
    } catch (Exception e) {
     System.out.println("Caught exception "+e);
    }
  }

}

