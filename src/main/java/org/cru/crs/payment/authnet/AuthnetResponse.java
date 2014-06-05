/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.cru.crs.payment.authnet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthnetResponse
{

	private String rawResponse = null;
    private List<String> response = new ArrayList<String>();
    private ResponseCode responseCode;
    private Integer reasonCode;
    private String reasonText = "";
    private String version = "3.1";
    
    //maximum number of field positions in response. there are more, but currently none are used.
    private static final int MAX_POSITION = 40; 

    //constant names for response fields
    public static final int RESPONSE_CODE           = 1;
    public static final int RESPONSE_SUBCODE        = 2;
    public static final int RESPONSE_REASON_CODE    = 3;
    public static final int RESPONSE_REASON_TEXT    = 4;
    public static final int APPROVAL_CODE           = 5;
    public static final int AUTHORIZATION_CODE      = 5;
    public static final int AVS_RESULT_CODE         = 6;
    public static final int TRANSACTION_ID          = 7;

    // 8 - 37 echoed from request
    public static final int INVOICE_NUMBER          = 8;
    public static final int DESCRIPTION             = 9;
    public static final int AMOUNT                  = 10;
    public static final int METHOD                  = 11;
    public static final int TRANSACTION_TYPE        = 12;
    public static final int CUSTOMER_ID             = 13;
    public static final int CARDHOLDER_FIRST_NAME   = 14;
    public static final int CARDHOLDER_LAST_NAME    = 15;
    public static final int COMPANY                 = 16;
    public static final int BILLING_ADDRESS         = 17;
    public static final int CITY                    = 18;
    public static final int STATE                   = 19;
    public static final int ZIP                     = 20;
    public static final int COUNTRY                 = 21;
    public static final int PHONE                   = 22;
    public static final int FAX                     = 23;
    public static final int EMAIL                   = 24;
    public static final int SHIP_TO_FIRST_NAME      = 25;
    public static final int SHIP_TO_LAST_NAME       = 26;
    public static final int SHIP_TO_COMPANY         = 27;
    public static final int SHIP_TO_ADDRESS         = 28;
    public static final int SHIP_TO_CITY            = 29;
    public static final int SHIP_TO_STATE           = 30;
    public static final int SHIP_TO_ZIP             = 31;
    public static final int SHIP_TO_COUNTRY         = 32;
    public static final int TAX_AMOUNT              = 33;
    public static final int DUTY_AMOUNT             = 34;
    public static final int FREIGHT_AMOUNT          = 35;
    public static final int TAX_EXEMPT_FLAG         = 36;
    public static final int PO_NUMBER               = 37;
    
    public static final int MD5_HASH                = 38;
    public static final int CID_RESPONSE_CODE       = 39;
    public static final int CAVV_RESPONSE_CODE    = 40;

    public AuthnetResponse(String resp)
    {
        this(resp, ",");
    }

    public AuthnetResponse(String rawResponse, String delim)
    {
        this.rawResponse = rawResponse;
        this.response = splitResp(rawResponse, delim);
        setApproval();
    }

    private void setApproval()
    {
        String responseCode = response.get(RESPONSE_CODE);
		this.responseCode = ResponseCode.forCode(responseCode);
        if (this.responseCode == null)
        {
        	throw new IllegalStateException("Bad response code: " + responseCode);
        }
        this.reasonCode = new BigDecimal(response.get(RESPONSE_REASON_CODE)).intValue();
        this.reasonText = response.get(RESPONSE_REASON_TEXT);
    }

    public void setVersion(String version)
    {
        if (version != null && version.length() > 0)
        {
            if (version.equals("3.0") || version.equals("3.1")) 
            {
                this.version = version;
            }
        }
        else
        {
        	throw new IllegalArgumentException("Version must be 3.0 or 3.1");
        }
    }

    /**
     * 
     * @return the result of the transaction
     */
    public ResponseCode getResponseCode()
    {
        return this.responseCode;
    }

    /**
     * 
     * @return A code representing more details about the result of the transaction.
     * See http://www.authorize.net/support/AIM_guide.pdf for details.
     */
    public Integer getReasonCode()
    {
        return this.reasonCode;
    }

    public String getReasonText()
    {
        return this.reasonText;
    }

    public String getResponseField(int position)
    {
    	if (response.size() < 39 )
    	{
            throw new RuntimeException("response had less than 39 fields! (" + response.size() + ")");
        }
        if (version.equals("3.0"))
        {
            if (position == CID_RESPONSE_CODE) 
            {
                return "M";
            }
        }
        if(position < 1 || position >= response.size())
        {
            return "unknown_field";
        }
        else
        {
            return response.get(position);
        }
    }

    public String getRawResponse() 
    {
        return this.rawResponse;
    }

    @Override
    public String toString()
    {
        return response.toString();
    }

    /**
     * @return The six-digit alphanumeric transactionResult or approval code. 
     */
	public String getAuthorizationCode()
	{
		return getResponseField(AUTHORIZATION_CODE);
	}

	/**
	 * This number identifies the transaction in the system and can be used to submit a modification of 
	 * this transaction at a later time, such as voiding, crediting or capturing the transaction.
	 * @return the Transaction ID
	 */
	public String getTransactionID()
	{
		requireApproved();
		return getResponseField(TRANSACTION_ID);
	}

	/**
	 * 
	 * @return the results of Card Code verification
	 */
	public CardCodeResponse getCardCodeResponse()
	{
		return CardCodeResponse.forCode(getResponseField(CID_RESPONSE_CODE));
	}

	/**
	 * 
	 * @return the result of Address Verification System (AVS) checks:
	 */
	public AVSResult getAVSResultCode()
	{
		requireApproved();
		return AVSResult.forCode(getResponseField(AVS_RESULT_CODE));
	}
	
	/**
	 * 
	 * @return the results of cardholder authentication verification
	 */
	public CAVVResponse getCAVVResponse()
	{
		return CAVVResponse.forCode(getResponseField(CAVV_RESPONSE_CODE));
	}

	/**
	 * 
	 * @return the amount of the transaction, if there is one; null otherwise
	 */
	public BigDecimal getAmount()
	{
		String amount = getResponseField(AMOUNT);
		return amount == null ? null : new BigDecimal(amount);
	}

	private void requireApproved()
	{
		if (!isApproved())
		{
			throw new IllegalStateException("Transaction was not approved");
		}
	}

	public boolean isApproved()
	{
		return ResponseCode.APPROVED.equals(getResponseCode());
	}

	public boolean isDeclined()
	{
		return ResponseCode.DECLINED.equals(getResponseCode());
	}
	
	public boolean isError()
	{
		return ResponseCode.ERROR.equals(getResponseCode());
	}

	public boolean isHeldForReview()
	{
		return ResponseCode.HELD_FOR_REVIEW.equals(getResponseCode());
	}

    private static List<String> splitResp(String response, String delim) {
    	List<String> out = new ArrayList<String>(MAX_POSITION);
        out.add("empty");
        out.addAll(Arrays.asList(response.split(delim, -1)));
        return out;
    }

    public enum AVSResult
    {
    	ADDRESS_NOT_ZIP("A", "Address (Street) matches, ZIP does not"),
    	ADDRESS_NOT_PROVIDED("B", "Address information not provided for AVS check"),
    	ERROR("E", "AVS error"),
    	NON_US_BANK("G", "Non-U.S. Card Issuing Bank"),
    	NO_MATCH("N", "No Match on Address (Street) or ZIP"),
    	NOT_APPLICABLE("P", "AVS not applicable for this transaction"),
    	RETRY("R", "System unavailable or timed out"),
    	NOT_SUPPORTED("S", "Service not supported by issuer"),
    	UNAVAILABLE("U", "Address information is unavailable"),
    	ZIP9_NOT_ADDRESS("W", "9 digit ZIP matches, Address (Street) does not"),
    	MATCH_9("X", "Address (Street) and 9 digit ZIP match"),
    	MATCH_5("Y", "Address (Street) and 5 digit ZIP match"),
    	ZIP5_NOT_ADDRESS("Z", "5 digit ZIP matches, Address (Street) does not");
		
		private String code;
		private String description;
		
		private AVSResult(String code, String description)
		{
			this.code = code;
			this.description = description;
		}

		public static AVSResult forCode(String responseField)
		{
			for (AVSResult avsResult : AVSResult.values())
			{
				if (avsResult.code.equals(responseField))
				{
					return avsResult;
				}
			}
			return null;
		}

		public String getCode()
		{
			return code;
		}

		public String getDescription()
		{
			return description;
		}
    }
    
	public enum CardCodeResponse
	{
		MATCH("M"),
		NO_MATCH("N"),
		NOT_PROCESSED("P"),
		SHOULD_HAVE_BEEN_PRESENT("S"),
		ISSUER_UNABLE_TO_PROCESS_REQUEST("U");

		private String code;
		
		CardCodeResponse(String code)
		{
			this.code = code;
		}
		
		public static CardCodeResponse forCode(String code)
		{
			for (CardCodeResponse cardCode : CardCodeResponse.values())
			{
				if (cardCode.code.equals(code))
				{
					return cardCode;
				}
			}
			return null;
		}
	}

    public enum ResponseCode
    {
    	APPROVED("1"), 
    	DECLINED("2"), 
    	ERROR("3"),
    	HELD_FOR_REVIEW("4");
    	
    	private String code;
    	
    	private ResponseCode(String code)
    	{
			this.code = code;
		}

		public static ResponseCode forCode(String code)
		{
			for (ResponseCode response : ResponseCode.values())
			{
				if (response.code.equals(code))
				{
					return response;
				}
			}
			return null;
    	}

		public String getCode()
		{
			return code;
		}
    }
    
    public enum CAVVResponse
    {
    	ERRONEOUS_DATA("0", "CAVV not validated because erroneous data was submitted"),
    	FAILED("1", "CAVV failed validation"),
    	PASSED("2", "CAVV passed validation"),
    	ISSUER_ATTEMPT_INCOMPLETE("3", "CAVV validation could not be performed; issuer attempt incomplete"),
    	ISSUER_ATTEMPT_ERROR("4", "CAVV validation could not be performed; issuer system error"),
    	FAILED_ISSUER_AVAILABLE("7", "CAVV attempt - failed validation - issuer available (U.S-issued card/non-U.S. acquirer"),
    	PASSED_ISSUER_AVAILABLE("8", "CAVV attempt - passed validation - issuer available (U.S-issued card/non-U.S. acquirer"),
    	FAILED_ISSUER_UNAVAILABLE("9", "CAVV attempt - failed validation - issuer unavailable (U.S-issued card/non-U.S. acquirer"),
    	PASSED_ISSUER_UNAVAILABLE("A", "CAVV attempt - passed validation - issuer unavailable (U.S-issued card/non-U.S. acquirer"),
    	PASSED_NO_LIABILITY("B", "CAVV passed validation, information only, no liability shift");
    	
    	private String code;
    	private String description;
		private CAVVResponse(String code, String description)
		{
			this.code = code;
			this.description = description;
		}
		public String getCode()
		{
			return code;
		}
		public String getDescription()
		{
			return description;
		}
		public static CAVVResponse forCode(String responseField)
		{
			return null;
		}
    }
}
