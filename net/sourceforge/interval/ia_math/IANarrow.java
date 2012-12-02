package net.sourceforge.interval.ia_math;

/**
 * IANarrow.java 
 *   -- classes implementing narrowing of arithmetic and elementary functions,
 *      as part of the "ia_math library" version 0.1beta1, 10/97
 * 
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class RealIntervalNarrow contains methods for narrowing
 * the arithmetic operations and elementary functions.
 */

import static net.sourceforge.interval.ia_math.IAMath.*;
import net.sourceforge.interval.ia_math.exceptions.IANarrowingFaildException;
import sun.awt.resources.awt;
//import static net.sourceforge.interval.ia_math.IAMath.intersect;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class IANarrow {

	private static boolean wasNarrowed(RealInterval newVal, RealInterval oldVal) throws IANarrowingFaildException {
		if (newVal == null)
			throw new IANarrowingFaildException(); // incompatible intervals
		if (newVal.wid() < oldVal.wid())
			return true;
		return false;
	}

	private static boolean wasNarrowed(	RealInterval one, RealInterval two, 
											RealInterval new1, RealInterval new2
									) throws IANarrowingFaildException {
		return wasNarrowed(new1, one) || wasNarrowed(new2, two);
	}
	private static boolean wasNarrowed(	RealInterval one, RealInterval two, RealInterval three, 
										RealInterval new1, RealInterval new2, RealInterval new3
											) throws IANarrowingFaildException {	
		return wasNarrowed(new1, one) || wasNarrowed(new2, two) || wasNarrowed(new3, three);
	}
	public static boolean narrowAdd(RealInterval[] ii) 	throws IANarrowingFaildException {
		assert ii.length == 3;
		RealInterval res = ii[0]; 
		RealInterval a = ii[1];
		RealInterval b = ii[2];
		RealInterval newRes = intersect(add(a, b), res);
		RealInterval newA   = intersect(sub(res, b), a);
		RealInterval newB   = intersect(sub(res, a), b);
		
		if ( wasNarrowed(res, a, b, newRes, newA, newB) ) {
			ii[0] = newRes;
			ii[1] = newA;
			ii[2] = newB;
			return true;
		}
		return false;
	}

	public static boolean narrowSub(RealInterval[] ii) throws IANarrowingFaildException {
		RealInterval tmp[] = new RealInterval[] { ii[1], ii[0], ii[2] };
		boolean success = narrowAdd(tmp);
		if (success) {
			ii[1] = tmp[0];
			ii[0] = tmp[1];
			ii[2] = tmp[2];
		}
		return success;
	}

	/* z = x*y */
	public static boolean narrowMul(RealInterval[] aWayToPassPointersToFunction)
			throws IANarrowingFaildException {
		assert aWayToPassPointersToFunction.length == 3;
		RealInterval res = aWayToPassPointersToFunction[0], 
					a = aWayToPassPointersToFunction[1], 
					b = aWayToPassPointersToFunction[2];
		RealInterval newRes =intersect(mul(a, b), res);
		RealInterval newA =  intersect(div(res, b), a);
		RealInterval newB =  intersect(div(res, a), b);
		if ( wasNarrowed(res, a, b, newRes, newA, newB) ) {
			aWayToPassPointersToFunction[0] = newRes;
			aWayToPassPointersToFunction[1] = newA;
			aWayToPassPointersToFunction[2] = newB;
			return true;
		}
		return false;			
	}

	public static boolean narrowDiv(RealInterval[] aWayToPassPointersToFunction)
			throws IANarrowingFaildException {
		assert aWayToPassPointersToFunction.length == 3;
		RealInterval tmp[] = new RealInterval[] { 	aWayToPassPointersToFunction[1], 
													aWayToPassPointersToFunction[2], 
													aWayToPassPointersToFunction[0]  };
		boolean success = narrowMul(tmp);
		if (success) {
			aWayToPassPointersToFunction[1] = tmp[0];
			aWayToPassPointersToFunction[2] = tmp[1];
			aWayToPassPointersToFunction[0] = tmp[2];
		}
		return success;
	}

  public static boolean narrowNegate(RealInterval[] ii) throws IANarrowingFaildException {
	  RealInterval res = ii[0], b = ii[1];
  	  res = intersect(res, uminus(b) );
  	  b = intersect(b, uminus(res) );
  	  res = intersect(res, b);

  	  if (wasNarrowed(ii[0], res) || wasNarrowed(ii[1], res)) {
  		  ii[0] = ii[1] = res;
  		  return true;
  	  }
  	  return false;
  }

	public static boolean narrowExp(RealInterval[] ii) throws IANarrowingFaildException {
		RealInterval r = ii[0], a = ii[1];
		RealInterval newR = intersect(exp(a), r);
		RealInterval newA = intersect(log(r), a);
		if ( wasNarrowed(r, a, newR, newA) ) {
			ii[0] = newR;
			ii[1] = newA;
			return true;
		}
		return false;
	}

	public static boolean narrowLog(RealInterval[] ii) throws IANarrowingFaildException {
		RealInterval tmp[] = new RealInterval[] { ii[1], ii[0] };
		boolean success = narrowExp(tmp);
		if (success) {
			ii[0] = tmp[1];
			ii[1] = tmp[0];
			return true;
		}
		return false;
	}

	public static boolean narrowSin(RealInterval r, RealInterval a)
			throws IANarrowingFaildException {
		RealInterval newR = intersect(sin(a), r);
		RealInterval newA = intersect(asin(r), a);
		return wasNarrowed(r, a, newR, newA);
	}

	public static boolean narrowCos(RealInterval r, RealInterval a)
			throws IANarrowingFaildException {
		RealInterval newR = intersect(cos(a), r);
		RealInterval newA = intersect(acos(r), a);
		return wasNarrowed(r, a, newR, newA);
	}

	public static boolean narrowTan(RealInterval r, RealInterval a)
			throws IANarrowingFaildException {
		RealInterval newR = intersect(tan(a), r);
		RealInterval newA = intersect(atan(r), a);
		return wasNarrowed(r, a, newR, newA);
	}
  /*	  
  // res = asin(a)
  public static boolean 
    narrow_asin(RealInterval res, RealInterval a) {
    try {
         b.intersect(new RealInterval(-1.0,1.0));
         a.intersect(IAMath.asin(b));
         b.intersect(IAMath.sin(a));
         return true;
    } catch (IAException e) {
      return false;
    }
  }

  // a = acos(b)
  public static boolean 
    narrow_acos(RealInterval b,RealInterval a) {
	  throw new NotImplementedException();
	  /*	  
    try {
         b.intersect(new RealInterval(-1,1));
         a.intersect(IAMath.acos(b));
         b.intersect(IAMath.cos(a));
         return true;
    } catch (IAException e) {
      return false;
    }
    }

	// a = atan(b)
	public static boolean narrow_atan(RealInterval b, RealInterval a) {
	}
*/  

	/**
	 * z = x^y, where y is an integer
	 * 
	 * @throws IANarrowingFaildException
	 */
	public static boolean narrowPower(RealInterval r, RealInterval x, int y)
			throws IANarrowingFaildException {
		RealInterval newR = intersect(power(x, y), r);

		RealInterval possibleX = root(r, y);
		// possibleX = ((+/-x)^4)^1/4
		if (y % 2 == 0) {
			if (possibleX.lo() > x.hi()) // [x] .. 0 .. [posX]
				possibleX = uminus(possibleX);
			else if (x.contains(possibleX) && x.contains(uminus(possibleX))) {
				// [ x_lo .. -[posX] .. 0 .. [posX] .. x_hi ]
				possibleX = new RealInterval(-possibleX.hi(), possibleX.hi());
			}
		}
		RealInterval newX = intersect(possibleX, x);
		return wasNarrowed(r, x, newR, newX);
	}

	/**
	 * z = x^y,
	 * 
	 * @throws IANarrowingFaildException
	 */
	public static boolean narrowPow(RealInterval[] aWayToPassPointersToFunction) throws IANarrowingFaildException {
		RealInterval r = aWayToPassPointersToFunction[0];
		RealInterval x = aWayToPassPointersToFunction[1];
		RealInterval y = aWayToPassPointersToFunction[2];
		
		RealInterval newR = intersect(power(x, y), r);
		RealInterval newX = intersect(root(r, y), x);
		RealInterval possibleY = div(log(r), log(x));
		RealInterval newY = intersect(possibleY, y);
		if ( wasNarrowed(r, x, y, newR, newX, newY) ) {
			aWayToPassPointersToFunction[0] = newR;
			aWayToPassPointersToFunction[1] = newX;
			aWayToPassPointersToFunction[2] = newY;
			return true;
		}
		return false;			
	}
      
/*
public static boolean narrow_semi(
       RealInterval a,RealInterval b,RealInterval c) {
    return false; //true;
  }
  
  public static boolean narrow_colon_equals(
       RealInterval a,RealInterval b,RealInterval c) {
    b.lo = c.lo; b.hi = c.hi;
    return b.nonEmpty();
  }
*/
  
	public static boolean narrowEquals(RealInterval[] ii) throws IANarrowingFaildException {
		assert ii.length == 2;
		RealInterval newVal = intersect(ii);
		boolean success = wasNarrowed(newVal, ii[0]) || wasNarrowed(newVal, ii[1]);
		if (success) {
			ii[0] = ii[1] = newVal;
		}
		return success;
	}
/*  
  public static boolean 
  narrow_eq(RealInterval a,RealInterval b,RealInterval c) {
    if ((b.lo==b.hi) && b.equals(c)) {
      a.lo = 1.0; a.hi = 1.0; 
      return(true);
    }
    else
      try {
        b.intersect(c);
        c.intersect(b);
        return true;
    } catch (IAException e) {
        return false;
    }
  }
*/

	/* x < y */
  /*
  public static boolean narrow_lt(
       RealInterval result,RealInterval x,RealInterval y) {
    try {
       if (y.lo < x.lo) y.lo = x.lo;
       if (x.hi > y.hi) x.hi = y.hi;
       if (y.hi <= x.lo)
         return false;
       else if (x.hi < y.lo) {
         result.lo = 1.0; result.hi=1.0;
       }
       else {
         result.intersect(new RealInterval(0.0,1.0));
       }
       return(x.nonEmpty()&&y.nonEmpty());
    } catch (IAException e) {
      return false;
    }
  }

  public static boolean narrow_le(
       RealInterval r,RealInterval x,RealInterval y) {
    try {
       if (y.lo <= x.lo) y.lo = x.lo;
       if (x.hi >= y.hi) x.hi = y.hi;
       if (y.hi < x.lo)
         return false;
       else if (x.hi <= y.lo) {
         r.lo = 1.0; r.hi=1.0;
       }
       else {
         r.intersect(new RealInterval(0.0,1.0));
       }
       return(x.nonEmpty()&&y.nonEmpty());
    } catch (IAException e) {
      return false;
    }
  }

  public static boolean narrow_gt(
       RealInterval r,RealInterval x,RealInterval y) {
    return narrow_lt(r,y,x);
  }

  public static boolean narrow_ge(
       RealInterval r,RealInterval x,RealInterval y) {
    return narrow_le(r,y,x);
  }

  public static boolean narrow_ne(
       RealInterval r,RealInterval x,RealInterval y) {
    return ((x.lo < x.hi) || (y.lo < y.hi) || (x.lo != y.lo));
  }
*/

}

