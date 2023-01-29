import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jade.core.Agent;
import jade.core.AID;


public class WebScrapper extends Agent{
	public static void main(String[] args) {
		
		
		System.out.println("Web Scrapper Running");
		System.out.println("----------------------");
		
		
		//Takes user input on which GPU model to search for
		Scanner scanner = new Scanner(System.in);
		System.out.print("What GPU model are you searching for?");
		String searchTerm = scanner.nextLine();
		
		//Initialize the link for 1st page
		String url = "https://www.newegg.com/p/pl?d="+searchTerm+"&N=4131&Order=1";
		
		String link = "";
		String itemName = "";
		String priceString = "";
		
		try {
			/* Pages */
			//Retrieve the HTML of the search results page
			Document doc = Jsoup.connect(url).get(); 
			Element pageTextElement = doc.select(".list-tool-pagination-text strong").first();
			
			String pageText = pageTextElement.text();
			
			int pages = Integer.parseInt(pageText.split("/")[1]);
	     
			for (int i = 2; i <= pages; i++) {
				url = "https://www.newegg.com/p/pl?d="+searchTerm+"&N=4131&Order=1&page="+i;
				doc = Jsoup.connect(url).get(); 
			}
			
			//Ensures that only items that are sold get scrapped
			Element div = doc.selectFirst(".item-cells-wrap.border-cells.items-grid-view.four-cells.expulsion-one-cell");
			Pattern pattern = Pattern.compile(searchTerm);
			Elements items = div.select("*:containsOwn(" + pattern + ")");
			
			for (Element item : items) {
				
				
				//Find itemName
			    itemName = item.text();
			    
			    //Stores the tagName of the item's name
			    String parentTagName = item.tagName();
			    
			    //If the parent of item is not a link, skip it
			    if (!parentTagName.equals("a")) {
		    	continue;
			    }
			    
			    //Find link
			    link = item.attr("href");
		    	
			    try {
				    //Find price 
					Element nextParent = item.closest(".item-container");
					
					Element priceElement = nextParent.select(".price-current strong").first();
					String price = priceElement.text();
					
					int priceInt = Integer.parseInt(price.replaceAll(",", ""));
					priceString = Integer.toString(priceInt);
			    } catch (Exception e) {
			    	System.out.println("Unable to find price");
				}
				
				System.out.println(searchTerm + ";" + itemName + ";$" + priceString + ";" + link);
				
				//Write to CSV File
				
				try {
					// Open the file in append mode.
			        FileWriter fw = new FileWriter("data.csv",true);
			        PrintWriter out = new PrintWriter(fw);

			        // Write the 4 datas into CSV file
			        out.println(searchTerm + ";" + itemName + ";$" + priceString + ";" + link);   // Step 3

			        // Close the file.
			        out.close();  // Step 4
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("----------------------");
		System.out.println("Web Scraping for completed for GPU model: " + searchTerm);
	}

}


