package com.clecs.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Token implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// "access_token":
		// "RB0OEd0hHR315LapD4_mlum7WPYJUC9IhmitoNxpxIVej4lYfOcPHX9MBYf1MpCBczo-5xvWumwuw-b8oSwUVllIDbpPCkInsEc--MU3S_GSUmXGzJtW2Gkdk4JFjhjVIqfBRBoLDcArDPM5bELiIJijgp0BGIMuROdXAWmw1Fw2mB-fgs4wCBjdRlqmr6TQmgZrxR6Jcvtfpe-w4R1kKoRk-63W0Kly0PcniCvVphuV5D32C7Pn89SYVnT3JK3GZTlVJlwZ0gkri1tgtdbY3vrXvP86GLSrITAIjN-WkLIqbSXa3Vcc60CXH5wBovlfm11BnTj3rJuVSYxy4yCrhRCrYeaTq9ba0A2HvUY-IG-enBeHo99J4fZFHD5y_bRcnmFYtCBcjmjhFrmuUeS1RCrg_goCIzLHJZKsRMryIryxeNZjguNVGxCvhwFgNzPZ7Vsj-JCpVC5W8SbjTCrbno2JeCVrA9QJ4-oOqslo5yk",
		// "token_type": "bearer",
		// "expires_in": 1209599,
		// "userName": "Mike",
		// ".issued": "Sun, 04 Jan 2015 17:01:25 GMT",
		// ".expires": "Sun, 18 Jan 2015 17:01:25 GMT"
		
		@SerializedName("access_token")
		String accessToken;
		@SerializedName("token_type")
		String tokenType;
		@SerializedName("expires_in")
		long expiresIn;
		@SerializedName("userName")
		String userName;
		@SerializedName(".issued")
		String issued;
		@SerializedName(".expires")
		String expires;

		public String getAccessToken()
			{
				return accessToken;
			}

		public void setAccessToken(String accessToken)
			{
				this.accessToken = accessToken;
			}

		public String getTokenType()
			{
				return tokenType;
			}

		public void setTokenType(String tokenType)
			{
				this.tokenType = tokenType;
			}

		public long getExpiresIn()
			{
				return expiresIn;
			}

		public void setExpiresIn(long expiresIn)
			{
				this.expiresIn = expiresIn;
			}

		public String getUserName()
			{
				return userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
			}

		public String getIssued()
			{
				return issued;
			}

		public void setIssued(String issued)
			{
				this.issued = issued;
			}

		public String getExpires()
			{
				return expires;
			}

		public void setExpires(String expires)
			{
				this.expires = expires;
			}

	}
