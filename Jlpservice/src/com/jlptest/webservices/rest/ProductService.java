package com.jlptest.webservices.rest;

import java.awt.Color;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.glassfish.jersey.client.ClientConfig;



@Path("/ProductService") 
public class ProductService {
private static final String webServiceURI = "https://jl-nonprod-syst.apigee.net/v1/categories/600001506/products?key=2ALHCAAs6ikGRBoy6eTHA58RaG097Fma";
private static Color color;
public static final String POUND = "\u00A3";
public static final String SHOWWASNOW = "ShowWasNow";
public static final String SHOWWASTHENNOW = "ShowWasThenNow";
public static final String SHOPERCDSCOUNT = "ShowPercDscount";
public final static Color purple = new Color(128,0,128);

@GET 
@Path("/productDetails") 
@Produces(MediaType.APPLICATION_JSON) 
public HashMap<String,List<ProductDetails>> getProductDetails(@QueryParam("labelType") String labelType){
	
	
	List<ProductDetails> productList=null;	
	HashMap<String,List<ProductDetails>> finalList = new HashMap<String,List<ProductDetails>>();
	TreeMap<Float,ProductDetails> sortVal = new TreeMap<Float,ProductDetails>();
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		URI serviceURI = UriBuilder.fromUri(webServiceURI).build();
		 WebTarget webTarget = client.target(serviceURI);
		 Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		 Response response = invocationBuilder.get();
		JSONObject results = response.readEntity(JSONObject.class);	
		try {		
			JSONArray JSONResults = results.getJSONArray("products");		
			Iterator it = JSONResults.iterator();
			while (it.hasNext()){
				ProductDetails productDetails = new ProductDetails();	
				String now;
				JSONObject thisProduct = (JSONObject) it.next();			
				JSONObject price = thisProduct.getJSONObject("price");
				 Object nowObject = price.get("now");
				if(nowObject instanceof String){
					 now = price.getString("now");
				}else{
					JSONObject priceJson = (JSONObject) nowObject;
					now = priceJson.getString("to");
				}
				
				String was = price.getString("was");				
				String then1 = price.getString("then1");
				String then2 = price.getString("then2");
				String finalPrice = calculatePrice(now);
				float priceReduction = 0f;
				Float percentageReduction = 0f;
				String percentageFormated=null;				
				if (!was.isEmpty() && !now.isEmpty()){
					float floatWas = Float.parseFloat(was);
					float floatNow = Float.parseFloat(now);
					priceReduction = floatWas-floatNow;				
					percentageReduction = (priceReduction/floatWas)*100;
					DecimalFormat df = new DecimalFormat("#.00");
				     percentageFormated = df.format(percentageReduction);
				}			
				
				if (priceReduction > 0){				
			     productDetails.setProductId(thisProduct.getString("productId"));
			     productDetails.setTitle(thisProduct.getString("title"));
				 JSONArray colorNames = thisProduct.getJSONArray("colorSwatches");				
				Iterator it2 = colorNames.iterator();
				List<HashMap<String,String>> colorList = new ArrayList<HashMap<String,String>>();
				while (it2.hasNext()){
					
					HashMap<String, String> colorValues = new HashMap<String, String>();
					JSONObject colorJson = (JSONObject) it2.next();
					if (null != colorJson.getString("color")){
					colorValues.put("color", colorJson.getString("color"));
					}
					else{
						colorValues.put("color", " ");
					}
					if (null != colorJson.getString("basicColor")){
					String colorName = colorJson.getString("basicColor");					
					Color color = getColor(colorName);
					String hexColour = null;
					if (color == null){
						hexColour = "N/A";
					}
					else{
					hexColour = Integer.toHexString(color.getRGB() & 0xffffff);
					if (hexColour.length() < 6) {
					    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
					  }
					}
					colorValues.put("rgbColor", hexColour);				
					}
					colorValues.put("skuid", colorJson.getString("skuId"));
					colorList.add(colorValues);
				}
				productDetails.setColorSwatches(colorList);		

				productDetails.setNowPrice(finalPrice);
				if (null == labelType){
					labelType = SHOWWASNOW;
				}
				String showPrice = "";
				switch (labelType){
				case SHOWWASNOW:
					if (!was.isEmpty()){
						showPrice = "Was "+calculatePrice(was);
						if (!now.isEmpty()){
							showPrice = showPrice+", " ;
						}
					}
					if (!now.isEmpty()){
						showPrice = showPrice+"now "+calculatePrice(now);
					}
					break;
				case SHOWWASTHENNOW:
					if (!was.isEmpty()){
						showPrice = "Was "+calculatePrice(was)+", ";
					}
					if (!then2.isEmpty()){
						showPrice = showPrice+"then "+calculatePrice(then2)+", ";
					}
					else if (!then1.isEmpty()){
						showPrice = showPrice+"then "+calculatePrice(then1)+", ";
					}
					if (!now.isEmpty()){
						showPrice = showPrice+"now "+calculatePrice(now);
					}
					break;
				case SHOPERCDSCOUNT:					
				{
						showPrice = percentageFormated+"% off - now "+calculatePrice(now);
					}
				}
				productDetails.setPriceLabel(showPrice);				
				sortVal.put(priceReduction,productDetails);				
				 productList=new ArrayList<ProductDetails>(sortVal.values());
				 Collections.reverse(productList);			
			
				}
			}
			
			finalList.put("products", productList);
		
		}catch (JSONException e) {			
			e.printStackTrace();
		}

	return finalList;
}

/*
method to get the color
*/
static Color getColor(String col) {
    switch (col.toLowerCase()) {
    case "black":
        color = Color.BLACK;        
        break;
    case "blue":
        color = Color.BLUE;
        break;
    case "cyan":
        color = Color.CYAN;
        break;
    case "darkgray":
        color = Color.DARK_GRAY;
        break;
    case "gray":
        color = Color.GRAY;
        break;
    case "grey":
        color = Color.GRAY;
        break;
    case "green":
    	color = Color.GREEN;
        break;
    case "yellow":
        color = Color.YELLOW;
        break;
    case "lightgray":
        color = Color.LIGHT_GRAY;
        break;
    case "magneta":
        color = Color.MAGENTA;
        break;
    case "orange":
        color = Color.ORANGE;
        break;
    case "pink":
        color = Color.PINK;
        break;
    case "red":
        color = Color.RED;
        break;
    case "white":
        color = Color.WHITE;
        break;
    case "purple":
        color = purple;
        break;
    default :
    	return null;
        }
    return color;
}
/*
 method to calculate price and append currency symbol
 */
public String calculatePrice(String priceValue){
	String finalPrice = null;
	try {
		if(Float.parseFloat(priceValue) > 10){
			Integer priceInt = Double.valueOf(priceValue).intValue();
			priceValue=priceInt.toString();						
		}
		finalPrice = POUND+priceValue;
	} catch (NumberFormatException e) {
		finalPrice = priceValue;
	}
	return finalPrice;
}
}
