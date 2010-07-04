package lmzr.photomngr.data;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;


/**
 * @author Laurent
 *
 */
public class RotationEffect implements Effect {
    //private Format inputFormat;
    private Format outputFormat;
    private Format[] inputFormats;
    private Format[] outputFormats;
    private Photo a_photo;

    /**
     * @param photo 
     * 
     */
    public RotationEffect(final Photo photo) {

    	a_photo = photo;
    	
        inputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

        outputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

    }

    /**
     * @see javax.media.Codec#getSupportedInputFormats()
     */
    public Format[] getSupportedInputFormats() {
    	return inputFormats;
    }

    /**
     * @see javax.media.Codec#getSupportedOutputFormats(javax.media.Format)
     */
    public Format [] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return outputFormats;
        }
        
        if (matches(input, inputFormats) != null) {
            return new Format[] { outputFormats[0].intersects(input) };
        }
		return new Format[0];
    }

    /**
     * @see javax.media.Codec#setInputFormat(javax.media.Format)
     */
    public Format setInputFormat(final Format input) {
		//inputFormat = input;
		return input;
    }

    /**
     * @see javax.media.Codec#setOutputFormat(javax.media.Format)
     */
    public Format setOutputFormat(final Format output) {
        if (output == null || matches(output, outputFormats) == null) return null;
        RGBFormat incoming = (RGBFormat) output;
        
        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();

        if (size == null)
            return null;
        if (maxDataLength < size.width * size.height * 3)
            maxDataLength = size.width * size.height * 3;
        if (lineStride < size.width * 3)
            lineStride = size.width * 3;
        if (flipped != Format.FALSE)
            flipped = Format.FALSE;
        
        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                                                        maxDataLength,
                                                        null,
                                                        frameRate,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        lineStride,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED));

        //System.out.println("final outputformat = " + outputFormat);
        return outputFormat;
    }


    /**
     * @see javax.media.Codec#process(javax.media.Buffer, javax.media.Buffer)
     */
    public int process(Buffer inBuffer, Buffer outBuffer) {
        int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
        validateByteArraySize(outBuffer, outputDataLength);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setFlags(inBuffer.getFlags());

        byte [] inData = (byte[]) inBuffer.getData();
        byte [] outData = (byte[]) outBuffer.getData();
        final RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        final RGBFormat vfOut = (RGBFormat) outBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        Dimension sizeOut = vfOut.getSize();
        int lineStrideIn = vfIn.getLineStride();

        int iw = sizeIn.width;
        int ih = sizeIn.height;
        int ow = sizeOut.width;
        int oh = sizeOut.height;
        int ocx = ow/2;
        int ocy = oh/2;
        int icx = iw/2;
        int icy = ih/2;
        int op = 0;

        if ( outData.length < iw*ih*3 ) {
            System.out.println("the buffer is not full");
            return BUFFER_PROCESSED_FAILED;
        }
            
        double vsin = Math.sin(a_photo.getIndexData().getRotation()*Math.PI/180);
        double vcos = Math.cos(a_photo.getIndexData().getRotation()*Math.PI/180);

        for ( int j = -ocy; j < oh-ocy; j++ )
            for ( int i = -ocx; i < ow-ocx; i++ ) {
                int x = (int)((vcos * i - vsin * j) + icx + 0.5);
                int y = (int)((vsin * i + vcos * j) + icy + 0.5);
                
                if ( x < 0 || x >= iw || y < 0 || y >= ih) {
                    outData[op++] = 0;
                    outData[op++] = 0;
                    outData[op++] = 0;
                } else {
                    int ip = lineStrideIn * y + x * 3;
                    outData[op++] = inData[ip++];
                    outData[op++] = inData[ip++];
                    outData[op++] = inData[ip++];
                }
            }
        

        return BUFFER_PROCESSED_OK;
        
    }
    
    /**
     * @see javax.media.PlugIn#getName()
     */
    public String getName() {
        return "Rotation Effect";
    }

    /**
     * @see javax.media.PlugIn#open()
     */
    public void open() {
    }

    /**
     * @see javax.media.PlugIn#close()
     */
    public void close() {
    }

    /**
     * @see javax.media.PlugIn#reset()
     */
    public void reset() {
    }

    /**
     * @see javax.media.Controls#getControl(java.lang.String)
     */
    public Object getControl(final String controlType) {
    	return null;
    }

    /**
     * @see javax.media.Controls#getControls()
     */
    public Object[] getControls() {
    	return null;
    }


    /**
     * @param in
     * @param outs
     * @return ???
     */
    Format matches(Format in, Format outs[]) {
		for (int i = 0; i < outs.length; i++) {
		    if (in.matches(outs[i]))
			return outs[i];
		}
		
		return null;
    }
    
    
    /**
     * @param buffer
     * @param newSize
     * @return ???
     */
    byte[] validateByteArraySize(Buffer buffer,int newSize) {
        Object objectArray=buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[]) {     // is correct type AND not null
            typedArray=(byte[])objectArray;
            if (typedArray.length >= newSize ) { // is sufficient capacity
                return typedArray;
            }

            byte[] tempArray=new byte[newSize];  // re-alloc array
            System.arraycopy(typedArray,0,tempArray,0,typedArray.length);
            typedArray = tempArray;
        } else {
            typedArray = new byte[newSize];
        }

        buffer.setData(typedArray);
        return typedArray;
    }

}

