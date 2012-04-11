package org.amneris.petstore;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.services.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Manifest;

public class ModuleUtils
{
	public static void loadApplicationDefaultsFromProperties(String properties, MappedConfiguration<String, Object> contributions)
	{
		Properties prop = new Properties();

		try
		{
			URL resource = ModuleUtils.class.getResource(properties);
			if (resource == null) throw new RuntimeException("Unable to load " + properties + " . Make shure the " +
					"file is in the root package and the filename starts with a slash /");
			prop.load(resource.openStream());
		} catch (IOException ioe)
		{
			throw new RuntimeException("Unable to load " + properties, ioe);
		}

		for (Object key : prop.keySet())
		{
			String value = prop.getProperty(key.toString());
			contributions.add(key.toString(), value);
		}
	}

	public static String getBuildVersion(Context context)
	{
		try
		{

			InputStream in = new FileInputStream(context.getRealFile("/META-INF/MANIFEST.MF"));
			Manifest mf = new Manifest(in);

			in.close();
			in = null;
			return mf.getMainAttributes().getValue("Implementation-Build");

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return Long.toHexString(System.currentTimeMillis());
	}

}
