/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package www.rdm.com;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

/**
 *
 * @author RANA_CSE
 */
class SemTextExtractionStrategy extends Chapter implements TextExtractionStrategy {
    private String text;
    Chapter a=new Chapter();
    
    @Override
    public String getResultantText() {
        return text;
    }

    @Override
    public void beginTextBlock() {
        
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
         try
        {
        text = renderInfo.getText();
        Vector curBaseline=renderInfo.getBaseline().getStartPoint();
        Vector topRight=renderInfo.getAscentLine().getEndPoint();
        Rectangle rect = new Rectangle(curBaseline.get(0), curBaseline.get(1), topRight.get(0), topRight.get(1));
        float curFontSize=rect.getHeight();

        text=text.replaceAll("\\s+","");
        text=text.toLowerCase();
    //    System.out.println(text+"   "+curFontSize);
        if(curFontSize!=ara[compp][compl])
        {
            compl=compl+1;
            ara[compp][compl]=curFontSize;
            sara[compp][compl]=text;
            ara1[compp]=compl;
    //        System.out.println(sara[compp][compl]=text);
        }
        else
        {
            sara[compp][compl]=sara[compp][compl]+text;
            ara1[compp]=compl;
        }
         
      
        if(chaptername.equals(sara[compp][compl])==true)
        {
           if(ara[compp][compl]>max)
           {
               max=ara[compp][compl];
               startforinput=compp;
           }
        }
        }
        catch(Exception e)
        {
            
        }
    }

    @Override
    public void endTextBlock() {
        
    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
        
    }       
}