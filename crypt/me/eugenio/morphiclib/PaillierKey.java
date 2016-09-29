package me.eugenio.morphiclib;

import java.math.BigInteger;

public class PaillierKey implements java.io.Serializable {
	/**
	 * Credits http://liris.cnrs.fr/~ohasan/pprs/paillierdemo/Paillier.java
	 */
	private static final long serialVersionUID = 1L;
	private BigInteger q;                   // a random prime (distinct from p)
    private BigInteger lambda;              // lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
    private BigInteger n;                   // n = p*q
    private BigInteger nsquare;             // nsquare = n*n
    private BigInteger g;                   // a random integer in Z*_{n^2}
    private BigInteger mu;                  // mu = (L(g^lambda mod n^2))^{-1} mod n, where L(u) = (u-1)/n
    
	public PaillierKey(BigInteger p, BigInteger q, BigInteger lambda, BigInteger n, BigInteger nsquare, BigInteger g, BigInteger mu) {
		this.p = p;
		this.q = q;
		this.lambda = lambda;
		this.n = n;
		this.nsquare = nsquare;
		this.g = g;
		this.mu = mu;
	}
	
    private BigInteger p;                   // a random prime
    public BigInteger getP() {
		return p;
	}
	public void setP(BigInteger p) {
		this.p = p;
	}
	public BigInteger getQ() {
		return q;
	}
	public void setQ(BigInteger q) {
		this.q = q;
	}
	public BigInteger getLambda() {
		return lambda;
	}
	public void setLambda(BigInteger lambda) {
		this.lambda = lambda;
	}
	public BigInteger getN() {
		return n;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	public BigInteger getNsquare() {
		return nsquare;
	}
	public void setNsquare(BigInteger nsquare) {
		this.nsquare = nsquare;
	}
	public BigInteger getG() {
		return g;
	}
	public void setG(BigInteger g) {
		this.g = g;
	}
	public BigInteger getMu() {
		return mu;
	}
	public void setMu(BigInteger mu) {
		this.mu = mu;
	}
    public void printValues()
    {
        System.out.println("p:       " + p);
        System.out.println("q:       " + q);
        System.out.println("lambda:  " + lambda);
        System.out.println("n:       " + n);
        System.out.println("nsquare: " + nsquare);
        System.out.println("g:       " + g);
        System.out.println("mu:      " + mu);
    }

}
