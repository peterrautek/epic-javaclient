import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mobilesynergies.epic.client.EpicClient;
import org.mobilesynergies.epic.client.remoteui.ArrayParameter;
import org.mobilesynergies.epic.client.remoteui.FloatParameter;
import org.mobilesynergies.epic.client.remoteui.IntParameter;
import org.mobilesynergies.epic.client.remoteui.Parameter;
import org.mobilesynergies.epic.client.remoteui.ParameterManager;
import org.mobilesynergies.epic.client.remoteui.ParameterMap;
import org.mobilesynergies.epic.client.remoteui.SpecializationException;
import org.mobilesynergies.epic.client.remoteui.StringParameter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import junit.framework.TestCase;


public class XmlTest extends TestCase {
	
	EpicClient mClient = null;
	
	@Before
	protected void setUp() throws Exception {
		mClient = new EpicClient();
		super.setUp();
	}
	

	@Test
	public void testParameterMap(){
		
		
		ParameterMap innermap = new ParameterMap();
		for(int i = 0; i<stringValues.length; i++){
			Parameter parameter = new StringParameter(stringValues[i]);
			innermap.putParameter("TestString"+i, parameter);
		}
		for(int i = 0; i<intValues.length; i++){
			Parameter parameter = new IntParameter(intValues[i]);
			innermap.putParameter("TestInteger"+i, parameter);
		}
		
		for(int i = 0; i<floatValues.length; i++){
			Parameter parameter = new FloatParameter(floatValues[i]);
			innermap.putParameter("TestFloat"+i, parameter);
		}
		
		
		ParameterMap outermap = new ParameterMap();
		
		outermap.putMap("innermap", innermap);
		for(int i = 0; i<stringValues.length; i++){
			Parameter parameter = new StringParameter(stringValues[i]);
			outermap.putParameter("TestString"+i, parameter);
		}
		for(int i = 0; i<intValues.length; i++){
			Parameter parameter = new IntParameter(intValues[i]);
			outermap.putParameter("TestInteger"+i, parameter);
		}
		
		for(int i = 0; i<floatValues.length; i++){
			Parameter parameter = new FloatParameter(floatValues[i]);
			outermap.putParameter("TestFloat"+i, parameter);
		}
		
		
		
		
		//get the xml from the parameter
		String xml = outermap.asXml("root");
		
		//create a stream 
		Reader reader = new StringReader(xml);
		XmlPullParser parser = null;
		try {
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			parser.setInput(reader);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //get the parameters from xml    
		ParameterMap p = null;
		try {
			p = (ParameterMap)ParameterManager.getInstance().parseXml(parser);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//compare the inner maps
		ParameterMap newinnermap = p.getMap("innermap", null);
		for(int i = 0; i<stringValues.length; i++){
			String newstring = newinnermap.getString("TestString"+i);
			assertTrue(newstring.equals(stringValues[i]));
		}
		for(int i = 0; i<intValues.length; i++){
			int newint = newinnermap.getInt("TestInteger"+i);
			assertTrue(newint==intValues[i]);
		}
		
		for(int i = 0; i<floatValues.length; i++){
			float newfloat = newinnermap.getFloat("TestFloat"+i);
			assertTrue(newfloat==floatValues[i]);
		}

		//compare the xml after parsing everything and regenerating it
		String newxml = p.asXml("root");
		assertTrue(newxml.equals(xml));
	}
	
	@Test
	public void testStringParameter(){
		for(int i = 0; i<stringValues.length; i++){
			String theString = stringValues[i];
			Parameter p = new StringParameter(theString);
			String xml = p.asXml("StringParameter"+i);
			
			Reader reader = new StringReader(xml);
			XmlPullParser parser = null;
			try {
				parser = XmlPullParserFactory.newInstance().newPullParser();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setInput(reader);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			StringParameter parameter = null; 
			try {
				parameter = (StringParameter) ParameterManager.getInstance().parseXml(parser);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String theNewString = parameter.getValue();
			assertTrue("String parameter "+i+" failed", theString.equals(theNewString));
			
	     
			
		}
		
	}
	
	@Test
	public void testIntParameter(){
		for(int i = 0; i<intValues.length; i++){
			float theInt = intValues[i]; 
			Parameter p = new IntParameter(intValues[i]);
			String xml = p.asXml("FloatParameter"+i);
			
			Reader reader = new StringReader(xml);
			XmlPullParser parser = null;
			try {
				parser = XmlPullParserFactory.newInstance().newPullParser();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setInput(reader);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			IntParameter parameter = null; 
			try {
				parameter = (IntParameter) ParameterManager.getInstance().parseXml(parser);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int theNewInt = parameter.getValue();
			assertTrue("Int parameter "+i+" failed", theInt == theNewInt);
		}
	}
	
	@Test
	public void testFloatParameter(){
		
		for(int i = 0; i<floatValues.length; i++){
			float theFloat = floatValues[i]; 
			Parameter p = new FloatParameter(floatValues[i]);
			String xml = p.asXml("FloatParameter"+i);
			
			Reader reader = new StringReader(xml);
			XmlPullParser parser = null;
			try {
				parser = XmlPullParserFactory.newInstance().newPullParser();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				parser.setInput(reader);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FloatParameter parameter = null; 
			try {
				parameter = (FloatParameter) ParameterManager.getInstance().parseXml(parser);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			float theNewFloat = parameter.getValue();
			assertTrue("Float parameter "+i+" failed", theFloat == theNewFloat);
		}
	}
	
	@Test
	public void testArrayParameter(){
		ArrayParameter parameter = new ArrayParameter();
		for(int i = 0; i<floatValues.length; i++){
			try {
				parameter.addEntry(new FloatParameter(floatValues[i]));
			} catch (SpecializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String xml = parameter.asXml("arrayparameter");
		
		Reader reader = new StringReader(xml);
		XmlPullParser parser = null;
		try {
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			parser.setInput(reader);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ArrayParameter newParameter = null; 
		try {
			newParameter = (ArrayParameter) ParameterManager.getInstance().parseXml(parser);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Parameter> list = newParameter.getArray();
		for (int i=0; i<floatValues.length; i++){
			float theNewFloat = ((FloatParameter)list.get(i)).getValue(); 
			assertTrue("Float parameter "+i+" failed", floatValues[i] == theNewFloat);
		}
	}
	
	String[] stringValues = {"this is a teststring", "a string containing a number 0123456789", "12345", "12.5f","-1234", "CAPITAL LETTERS", "asdfghjkl", "some tough characters <> & ! \" § $ % & / ( \\ ) = ? + * # ; - _ @ ö ü ä < > |", "AsDfGhJkL", "some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text some really long text"};
	int[] intValues = {10, 00001, 10000, 12345, 12, -1234, 55000, -55000, Integer.MAX_VALUE, Integer.MIN_VALUE, 0};
	float[] floatValues =  {1, 1000000, 0.00001f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.MIN_NORMAL, 0};
	
	

}
