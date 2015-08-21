package cn.com.poka.gzhquery.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class CxfUtil {
	private Properties p;
	public CxfUtil() {
		this("service.properties");
	}
	
	public CxfUtil(String path){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
	    this.p = new Properties();
	    try {
			p.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    if(in != null){
	    	try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	public void recieveTimeOutWrapper(Object o){
		int recTimeout = p.getProperty("recTimeout")==null?300:Integer.parseInt(p.getProperty("recTimeout"));
		int connTimeout = p.getProperty("connectTimeout")==null?300:Integer.parseInt(p.getProperty("connectTimeout"));;
		recieveTimeOutWrapper(o,recTimeout,connTimeout);
	}
	
	public void recieveTimeOutWrapper(Object o,int recTimeout,int connTimeout)
	{
		Conduit conduit = (ClientProxy.getClient(o).getConduit());

		HTTPConduit hc = (HTTPConduit) conduit;

		HTTPClientPolicy client = new HTTPClientPolicy();

		client.setReceiveTimeout(1000 *  recTimeout); // 超时时间。 该时间为响应超时。(秒)

	    client.setConnectionTimeout(1000 * connTimeout); // 的连接超时。（秒）
	    
		hc.setClient(client);
	}
	
	@SuppressWarnings("unchecked")
	public  <T> T  getCxfClient(Class<T> cls ,String url){
		 JaxWsProxyFactoryBean  factoryBean=new JaxWsProxyFactoryBean(); 
	        factoryBean.getInInterceptors().add(new LoggingInInterceptor()); 
	        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor()); 
	        factoryBean.setServiceClass(cls); 
	        factoryBean.setAddress(url); 
	        factoryBean.getInInterceptors().add(new LoggingInInterceptor());
	        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
	        return (T)factoryBean.create();
	}
	public  String getUrl(String ip,String port){
	System.out.println("ip:"+ip+"   port:"+port);
	   return p.getProperty("url").replace("${url.ip}", ip).replace("${url.port}", port);
	}
	public String getPort(){
		return p.getProperty("url.port");
	}
	
	
}
