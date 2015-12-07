package de.konradhoeffner.commons;

/** Generic class for Pairs. */
public class Pair<A,B>
{
	public final A	a;
	public final B	b;

	public A getA() {return a;}
	public B getB() {return b;}

	public Pair(A a, B b)
	{
		super();
		this.a = a;
		this.b = b;
	}

	@Override public String toString()
	{
		return "(" + a + ", " + b + ')';
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Pair<?,?>)) return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (a == null)
		{
			if (other.a != null) return false;
		}
		else if (!a.equals(other.a)) return false;
		if (b == null)
		{
			if (other.b != null) return false;
		}
		else if (!b.equals(other.b)) return false;
		return true;
	}
}