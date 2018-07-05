package single.practice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.Resource;
import single.practice.utils.StrUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  yWX243867
 * @version  [版本号, 2015年4月20日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public  class PropertiesLoader extends PropertyPlaceholderConfigurer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);
    
    private PropertiesProcessor propertiesPersister = new PropertiesProcessor();
    
    private Resource[] locations;
    
    @Override
    public void setLocation(Resource location)
    {
        this.locations = new Resource[] {location};
    }
    
    @Override
    public void setLocations(Resource[] locations)
    {
        this.locations = locations.clone();
    }
    
    @Override
    protected void loadProperties(Properties props)
        throws IOException
    {
        if (this.locations != null)
        {
            InputStream is = null;
            Reader reader = null;
            String filename = null;
            
            for (Resource location : this.locations)
            {
                is = null;
                try
                {
                    is = location.getInputStream();
                    reader = new InputStreamReader(is,"UTF-8");
                    
                    filename = (location instanceof AbstractFileResolvingResource) ? location.getFilename() : null;
                    if ((filename != null) && (filename.endsWith(".xml")))
                    {
                        this.propertiesPersister.loadFromXml(props, is);
                    }
                    else
                    {
                        this.propertiesPersister.load(props, reader);
                    }
                }
                catch (IOException ex)
                {
                	LOGGER.error(StrUtil.toOneLine("load File failed"));
                }
                finally
                {
                	try{
                        if (is != null){
                            is.close();
                        }
                	}catch(IOException e){
                		LOGGER.error(StrUtil.toOneLine("load File failed"));
                	}
                	try {
                		if (reader != null) {
                			reader.close();
                		}
					} catch (IOException e) {
						LOGGER.error(StrUtil.toOneLine("load File failed"));
					}
                }
            }
        }
    }
    
}
