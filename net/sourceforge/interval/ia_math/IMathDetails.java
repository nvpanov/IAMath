/**
 * 
 */
package net.sourceforge.interval.ia_math;

import static net.sourceforge.interval.ia_math.IAMath.*;
import net.sourceforge.interval.ia_math.exceptions.IAComputationalException;

/**
 * @author nvpanov
 * 
 */

class IMathDetails {

	protected static final RealInterval whole = new RealInterval();
	protected static final RealInterval zero = new RealInterval(0);
	protected static final RealInterval one_one = new RealInterval(-1, 1);
	protected static final RealInterval one = new RealInterval(1);

	protected static boolean isAnyoneThin(RealInterval x, RealInterval y) {
		 return (x.wid() == 0 || y.wid() == 0);
	}
	protected static RealInterval div_zero(RealInterval x) {
		if (x.lo() == 0 && x.hi() == 0)
			return new RealInterval(0); // let it bee so
		else
			return whole;
	}

	protected static RealInterval div_negative(RealInterval x, double yl) {
		if (yl >= 0)
			throw new IllegalArgumentException();
		double xl = x.lo();
		double xu = x.hi();
		if (xl == 0 && xu == 0)
			return zero;
		if (xu < 0) // [ <0 ] / [y, 0]
			return new RealInterval(RMath.div_lo(xu, yl),
					Double.POSITIVE_INFINITY);
		else if (xl < 0) // [ .. 0 .. ] / [y, 0]
			return whole;
		else
			// [ >0 ] / [y, 0]
			return new RealInterval(Double.NEGATIVE_INFINITY, RMath.div_hi(xl,
					yl));
	}

	protected static RealInterval div_positive(RealInterval x, double yu) {
		if (yu <= 0)
			throw new IllegalArgumentException();
		double xl = x.lo();
		double xu = x.hi();
		// nod needed here as the check is done in div(), but let be on a safe
		// side
		if (xl == 0 && xu == 0)
			return zero;
		if (xu < 0) // [ <0 ] / [0, y]
			return new RealInterval(Double.NEGATIVE_INFINITY, RMath.div_hi(xu,
					yu));
		else if (xl < 0) // [ .. 0 .. ] / [y, 0]
			return whole;
		else
			// [ >0 ] / [y, 0]
			return new RealInterval(RMath.div_lo(xl, yu),
					Double.POSITIVE_INFINITY);
	}

	protected static RealInterval div_non_zero(RealInterval x, RealInterval y) {
		if (y.contains(0))
			throw new IllegalArgumentException();
		double xl = x.lo();
		double xu = x.hi();
		double yl = y.lo();
		double yu = y.hi();
		if (xu < 0)
			if (yu < 0)
				return new RealInterval(RMath.div_lo(xu, yl), RMath.div_hi(xl,
						yu));
			else
				return new RealInterval(RMath.div_lo(xl, yl), RMath.div_hi(xu,
						yu));
		else if (xl < 0)
			if (yu < 0)
				return new RealInterval(RMath.div_lo(xu, yu), RMath.div_hi(xl,
						yu));
			else
				return new RealInterval(RMath.div_lo(xl, yl), RMath.div_hi(xu,
						yl));
		else if (yu < 0)
			return new RealInterval(RMath.div_lo(xu, yu), RMath.div_hi(xl, yl));
		else
			return new RealInterval(RMath.div_lo(xl, yu), RMath.div_hi(xu, yl));
	}

	protected static RealInterval sinRange(int a, int b) {
		switch (4 * a + b) {
		case 0:
			return (new RealInterval(-1.0, 1.0));
		case 1:
			return (new RealInterval(1.0, 1.0));
		case 2:
			return (new RealInterval(0.0, 1.0));
		case 3:
			return (new RealInterval(-1.0, 1.0));
		case 4:
			return (new RealInterval(-1.0, 0.0));
		case 5:
			return (new RealInterval(-1.0, 1.0));
		case 6:
			return (new RealInterval(0.0, 0.0));
		case 7:
			return (new RealInterval(-1.0, 0.0));
		case 8:
			return (new RealInterval(-1.0, 0.0));
		case 9:
			return (new RealInterval(-1.0, 1.0));
		case 10:
			return (new RealInterval(-1.0, 1.0));
		case 11:
			return (new RealInterval(-1.0, -1.0));
		case 12:
			return (new RealInterval(0.0, 0.0));
		case 13:
			return (new RealInterval(0.0, 1.0));
		case 14:
			return (new RealInterval(0.0, 1.0));
		case 15:
			return (new RealInterval(-1.0, 1.0));
		}
		//System.out.println("Internal error: unknown range for sinRange(" + a
		//		+ "," + b + ")");
		//throw new IAComputationalException(
		//		"Internal error: unknown range for sinRange(" + a + "," + b
		//				+ ")");
		 return new RealInterval(-1,1);
	}

	protected static RealInterval sin2pi0DI(double x) {
		return new RealInterval(RMath.sin2pi_lo(x), RMath.sin2pi_hi(x));
	}

	protected static RealInterval cos2pi0DI(double x) {
		return new RealInterval(RMath.cos2pi_lo(x), RMath.cos2pi_hi(x));
	}

	/*
	 * this returns an interval containing sin(x+a/4) assuming -1/4 <= x < 1/4,
	 * and a in {0,1,2,3}
	 */
	protected static RealInterval eval_sin2pi(double x, int a) {
		switch (a) {
		case 0:
			return sin2pi0DI(x);
		case 1:
			return cos2pi0DI(x);
		case 2:
			return uminus(sin2pi0DI(x));
		case 3:
			return uminus(cos2pi0DI(x));
		}
		System.out.println("ERROR in eval_sin2pi(" + x + "," + a + ")");
		throw new IAComputationalException("Internal error in eval_sin2pi(" + x
				+ "," + a + ")");
		// return new RealInterval();
	}

	protected static RealInterval sin2pi(RealInterval x) {
		// RealInterval r = new RealInterval(); //nvp: local var is never used
		RealInterval z = null;
		RealInterval y1 = null, y2 = null;
		int a = 0, b = 0;
		// double t1=0,t2=0; //nvp: local var is never used
		// double w; //nvp: local var is never used

		double m1, m2, n1, n2, z1, z2, width;
		int j1, j2;
		long mlo, mhi;

		// System.out.println("ENTERING sin2pi("+x+")");

		if (Double.isInfinite(x.lo) || Double.isInfinite(x.hi)) {
			return new RealInterval(-1.0, 1.0);
		}

		m1 = Math.rint(4 * x.lo);
		j1 = (int) Math.round(m1 - 4 * Math.floor(m1 / 4.0));
		z1 = RMath.sub_lo(x.lo, m1 / 4.0);
		n1 = Math.floor(m1 / 4.0);

		m2 = Math.rint(4 * x.hi);
		j2 = (int) Math.round(m2 - 4 * Math.floor(m2 / 4.0));
		z2 = RMath.sub_hi(x.hi, m2 / 4.0);
		n2 = Math.floor(m2 / 4.0);

		// System.out.println("in sin2pi: "+" x.lo="+x.lo+" x.hi="+x.hi);
		// System.out.println("         : "+" m1="+m1+" m2="+m2);
		// System.out.println("         : "+" z1="+z1+" z2="+z2);
		// System.out.println("         : "+" j1="+j1+" j2="+j2);
		// System.out.println("         : "+" n1="+n1+" n2="+n2);

		if ((z1 <= -0.25) || (z1 >= 0.25) || (z2 <= -0.25) || (z2 >= 0.25))
			return new RealInterval(-1.0, 1.0);

		mlo = (z1 >= 0) ? j1 : j1 - 1;
		mhi = (z2 <= 0) ? j2 : j2 + 1;

		width = (mhi - mlo + 4 * (n2 - n1));

		// System.out.println("         : "+" mlo="+mlo+" mhi="+mhi);
		// System.out.println("         : "+" width"+width);

		if (width > 4)
			return new RealInterval(-1.0, 1.0);

		y1 = eval_sin2pi(z1, j1);
		y2 = eval_sin2pi(z2, j2);

		z = new RealInterval(Math.min(y1.lo, y2.lo), Math.max(y2.hi, y1.hi)); // nvp 1/9/2012

		a = (int) ((mlo + 4) % 4);
		b = (int) ((mhi + 3) % 4);

		// System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
		// "\n  j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
		// "\n  w ="+width+" a="+a+" b="+b+"\n  sinRange="+sinRange(a,b));
		// if (r.lo < 0) a = (a+3)%4;
		// if (r.hi < 0) b = (b+3)%4;

		if (width <= 1)
			return z;
		RealInterval tmp = sinRange(a, b);
//		tmp = union(z, sinRange(a, b));
		tmp = new RealInterval(Math.min(z.lo, tmp.lo), Math.max(z.hi, tmp.hi)); // nvp 1/9/2012

		return tmp;
	}

	protected static RealInterval cos2pi(RealInterval x) {
		RealInterval z = null;
		RealInterval y1 = null, y2 = null;
		int a = 0, b = 0;

		double m1, m2, n1, n2, z1, z2, width;
		int j1, j2;
		long mlo, mhi;

		if (Double.isInfinite(x.lo) || Double.isInfinite(x.hi)) {
			return new RealInterval(-1.0, 1.0);
		}

		m1 = Math.rint(4 * x.lo);
		j1 = (int) Math.round(m1 - 4 * Math.floor(m1 / 4.0));
		z1 = RMath.sub_lo(x.lo, m1 / 4.0);
		n1 = Math.floor(m1 / 4.0);

		m2 = Math.rint(4 * x.hi);
		j2 = (int) Math.round(m2 - 4 * Math.floor(m2 / 4.0));
		z2 = RMath.sub_hi(x.hi, m2 / 4.0);
		n2 = Math.floor(m2 / 4.0);

		if ((z1 <= -0.25) || (z1 >= 0.25) || (z2 <= -0.25) || (z2 >= 0.25))
			return new RealInterval(-1.0, 1.0);

		mlo = (z1 >= 0) ? j1 : j1 - 1;
		mhi = (z2 <= 0) ? j2 : j2 + 1;

		width = (mhi - mlo + 4 * (n2 - n1));

		if (width > 4)
			return new RealInterval(-1.0, 1.0);

		y1 = eval_sin2pi(z1, (j1 + 1) % 4);
		y2 = eval_sin2pi(z2, (j2 + 1) % 4);

		z = new RealInterval(Math.min(y1.lo,y2.lo), Math.max(y1.hi,y2.hi));

		a = (int) ((mlo + 4 + 1) % 4);
		b = (int) ((mhi + 3 + 1) % 4);

		// System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
		// "\n  j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
		// "\n  w ="+width+" a="+a+" b="+b+"\n  sinRange="+sinRange(a,b));
		// if (r.lo < 0) a = (a+3)%4;
		// if (r.hi < 0) b = (b+3)%4;

		if (width <= 1)
			return z;
		RealInterval tmp0 = sinRange(a, b);
		RealInterval tmp = new RealInterval(Math.min(z.lo,tmp0.lo), Math.max(z.hi,tmp0.hi));
		return tmp;
	}

	protected static RealInterval tan2pi(RealInterval x) {
		return (div(sin2pi(x), cos2pi(x)));
	}

	/*
	 * protected static RealInterval asin2pi(RealInterval x) throws IAException
	 * { if (!x.isIntersects(one_one)) throw new IllegalArgumentException();
	 * RealInterval area = intersection(x, one_one);
	 * 
	 * return new RealInterval(RMath.asin2pi_lo(x.lo), RMath.asin2pi_hi(x.hi));
	 * }
	 * 
	 * public static RealInterval acos2pi(RealInterval x) { RealInterval z = new
	 * RealInterval(); z.lo = RMath.acos2pi_lo(x.hi); z.hi =
	 * RMath.acos2pi_hi(x.lo); return(z); }
	 * 
	 * public static RealInterval atan2pi(RealInterval x) { RealInterval z = new
	 * RealInterval(); z.lo = RMath.atan2pi_lo(x.lo); z.hi =
	 * RMath.atan2pi_hi(x.hi); return(z); }
	 */
	protected static RealInterval evenPower(RealInterval x, double y)
			throws IllegalArgumentException {
		double zlo, zhi;
		// System.out.println("evenPower: x^y with (x,y) = "+x+" "+y);

		if (y == 0.0)
			return one;
		else if (y > 0.0) {
			if (x.lo >= 0) {
				zlo = RMath.pow_lo(x.lo, y);
				zhi = RMath.pow_hi(x.hi, y);
			} else if (x.hi <= 0) {
				zlo = RMath.pow_lo(-x.hi, y);
				zhi = RMath.pow_hi(-x.lo, y);
			} else {
				zlo = 0.0;
				zhi = Math.max(RMath.pow_lo(-x.lo, y), RMath.pow_hi(x.hi, y));
			}
		} else if (y < 0.0) {
			return div(one, evenPower(x, -y));
		} else
			throw new IllegalArgumentException(
					"evenPower(X,y): y=Nan not allowed");

		// System.out.println("evenPower: computed x^y = ["+zlo+","+zhi+"]");

		return new RealInterval(zlo, zhi);
	}

	/**
	 * this is the Natural Interval extension of <code>sgn(x)*(|x|**y)<\code}
	 * where <code>x</code> is an interval and <code>y</code> is a double.
	 */
	public static RealInterval oddPower(RealInterval x, double y)
			throws IllegalArgumentException {
		double zlo, zhi;

		// System.out.println("oddPower: x^y with (x,y) = "+x+" "+y);

		if (y == 0.0) {
			if (x.lo > 0.0)
				return one;
			else if (x.hi < 0.0)
				return (new RealInterval(-1.0));
			else
				return one_one;
		} else if (y > 0.0) {
			if (x.lo >= 0) {
				zlo = RMath.pow_lo(x.lo, y);
				zhi = RMath.pow_hi(x.hi, y);
			} else if (x.hi <= 0) {
				zlo = -RMath.pow_hi(-x.lo, y);
				zhi = -RMath.pow_lo(-x.hi, y);
			} else {
				zlo = -RMath.pow_hi(-x.lo, y);
				zhi = RMath.pow_hi(x.hi, y);
			}
		} else if (y < 0.0) {
			return div(one, oddPower(x, -y));
		} else
			throw new IllegalArgumentException(
					"oddPower(X,y): X = NaN not allowed");

		// System.out.println("oddPower: computed x^y = ["+zlo+","+zhi+"]");

		return new RealInterval(zlo, zhi);

	}




	  /**
	   * this is the Natural Interval extension of <code>xpos**(1/y)<\code}
	   * where <code>x</code> is an interval and <code>xpos</code> is the
	   * set of positive numbers contained in x and
	   * <code>y</code> is a non-zero double.
	   */
	  protected static RealInterval evenRoot(RealInterval x, int y)
	    throws IllegalArgumentException
	  {
	    double ylo,yhi,zlo,zhi; 
	    //    System.out.println("evenRoot x^(1/y) with (x,y) = "+x+y);

	    if (y < 0)
	        return div(one,evenRoot(x, -y));
	    else if (y > 0) {
	      ylo = RMath.div_lo(1.0,y);
	      yhi = RMath.div_hi(1.0,y);

	      if (x.lo() >= 1)
	        zlo = RMath.pow_lo(x.lo(), ylo);
	      else if (x.lo() >=  0)
	        zlo = RMath.pow_lo(x.lo(), yhi);
	      else
	        zlo = 0.0;

	      if (x.hi() >= 1)
	        zhi = RMath.pow_hi(x.hi(), yhi);
	      else if (x.lo() >=  0) // ????
	        zhi = RMath.pow_hi( x.hi(), ylo);
	      else 
	        throw new IllegalArgumentException("evenRoot(X,y): X < 0 not allowed");

	      return new RealInterval(zlo,zhi);
	    }
	    else // y = 0, Nan...
	      throw new IllegalArgumentException("evenRoot(X,y): y = " + y + " not allowed");
	  }

	  /**
	   * this is the Natural Interval extension of <code>sgn(x)*|x|**(1/y)<\code}
	   * where <code>x</code> is an interval and
	   * <code>y</code> is a non-zero double.
	   */
	  protected static RealInterval oddRoot(RealInterval x, int y)
	    throws IllegalArgumentException
	  {
	    double ylo,yhi,zlo,zhi;

	    if (y < 0) 
		    return div(one, oddRoot(x, -y));
	    if (y > 0) {
	      ylo = RMath.div_lo(1.0,y);
	      yhi = RMath.div_hi(1.0,y);
	      if (x.lo >= 1.0)
	        zlo = RMath.pow_lo(x.lo,ylo);
	      else if (x.lo >=  0.0)
	        zlo = RMath.pow_lo(x.lo,yhi);
	      else if (x.lo >= -1.0) 
	        zlo = -RMath.pow_hi(-x.lo,ylo);
	      else 
	        zlo = -RMath.pow_hi(-x.lo,yhi);

	      if (x.hi >= 1.0)
	        zhi = RMath.pow_hi( x.hi,yhi);
	      else if (x.hi >=  0.0)
	        zhi = RMath.pow_hi( x.hi,ylo);
	      else if (x.hi >= -1.0) 
	        zhi = -RMath.pow_lo(-x.hi,yhi);
	      else 
	        zhi = -RMath.pow_lo(-x.hi,ylo);


	      return new RealInterval(zlo,zhi);
	    }
	    else
	      throw new IllegalArgumentException("oddRoot(X,y): y = " + y + " not allowed");
	  }


}
