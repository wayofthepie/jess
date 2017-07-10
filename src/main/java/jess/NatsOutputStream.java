package jess;

import jess.service.NatsService;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class NatsOutputStream extends OutputStream {

    private OutputStream delegate;
    private NatsService natsService;
    private StringBuilder builder;

    /**
     * The last processed character, or {@code -1} for the start of the stream.
     */
    private int previousCharacter = -1;

    NatsOutputStream(NatsService natsService, OutputStream delegate) {
        this.natsService = natsService;
        this.delegate = delegate;
        this.builder = new StringBuilder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        byte[] bytes = {(byte) b};
        parseAndPushEvent(bytes, 0, 1);
        delegate.write(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }

    private void parseAndPushEvent(byte[] b, int off, int len) {
        byte newlineCharacter = (byte) 0x0A;
        for (int i = off; i < off + len; i++) {
            if (previousCharacter == newlineCharacter) {
                String line = builder.toString();
                natsService.publish("log", line);
                builder = new StringBuilder();
                builder.append(new String(b, off, len));
            } else {
                if(b[i] != newlineCharacter)
                    builder.append(new String(b, off, len));
            }
            previousCharacter = b[i];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
