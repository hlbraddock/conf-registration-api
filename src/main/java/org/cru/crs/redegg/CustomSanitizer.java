package org.cru.crs.redegg;

import java.util.List;

import org.cru.redegg.recording.api.ParameterSanitizer;

import com.google.common.collect.ImmutableList;

public class CustomSanitizer implements ParameterSanitizer
{

	@Override
	public List<String> sanitizePostBodyParameter(String parameterName, List<String> parameterValues)
	{
		return sanitize(parameterName, parameterValues);
	}

	@Override
	public List<String> sanitizeQueryStringParameter(String parameterName, List<String> parameterValues)
	{
		return sanitize(parameterName, parameterValues);
	}

    private List<String> sanitize(String parameterName, List<String> parameterValues)
    {
        if (parameterName.equals("secret"))
        {
            return ImmutableList.of("<removed>");
        }
        else
        {
            return parameterValues;
        }
    }
}
