package org.lseixas.mineguerra_plugins.clientaudit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Codec v1: short protocol + UTF-8 length-prefixed strings + varint counts.
 * SHA-1 de pack: 20 bytes crus.
 */
public final class ClientAuditCodec {

    public static final String CHANNEL = "mineguerra:client_audit";
    private static final int MAX_STRING = 32767;
    private static final int SHA1_LEN = 20;

    private ClientAuditCodec() {
    }

    public static byte[] encode(ClientAuditPayload payload) throws IOException {
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(raw);
        out.writeShort(payload.protocol());
        writeUtf(out, payload.mcVersion());
        writeUtf(out, payload.loaderVersion());
        writeVarInt(out, payload.mods().size());
        for (ClientAuditPayload.ModEntry mod : payload.mods()) {
            writeUtf(out, mod.id());
            writeUtf(out, mod.version());
        }
        writeVarInt(out, payload.packs().size());
        for (ClientAuditPayload.PackEntry pack : payload.packs()) {
            writeUtf(out, pack.id());
            byte[] sha1 = pack.sha1() == null ? new byte[SHA1_LEN] : pack.sha1();
            if (sha1.length != SHA1_LEN) {
                throw new IOException("SHA-1 deve ter 20 bytes");
            }
            out.write(sha1);
        }
        writeUtf(out, payload.shader() == null ? "" : payload.shader());
        out.flush();
        return raw.toByteArray();
    }

    public static ClientAuditPayload decode(byte[] data) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        int protocol = in.readUnsignedShort();
        String mcVersion = readUtf(in);
        String loaderVersion = readUtf(in);
        int modCount = readVarInt(in);
        List<ClientAuditPayload.ModEntry> mods = new ArrayList<>(modCount);
        for (int i = 0; i < modCount; i++) {
            mods.add(new ClientAuditPayload.ModEntry(readUtf(in), readUtf(in)));
        }
        int packCount = readVarInt(in);
        List<ClientAuditPayload.PackEntry> packs = new ArrayList<>(packCount);
        for (int i = 0; i < packCount; i++) {
            String id = readUtf(in);
            byte[] sha1 = in.readNBytes(SHA1_LEN);
            if (sha1.length != SHA1_LEN) {
                throw new IOException("SHA-1 truncado");
            }
            packs.add(new ClientAuditPayload.PackEntry(id, sha1));
        }
        String shader = readUtf(in);
        return new ClientAuditPayload(protocol, mcVersion, loaderVersion, List.copyOf(mods), List.copyOf(packs), shader);
    }

    static void writeUtf(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_STRING) {
            throw new IOException("String longa demais");
        }
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    static String readUtf(DataInputStream in) throws IOException {
        int len = readVarInt(in);
        if (len < 0 || len > MAX_STRING) {
            throw new IOException("Tamanho de string invalido");
        }
        byte[] bytes = in.readNBytes(len);
        if (bytes.length != len) {
            throw new IOException("String truncada");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static void writeVarInt(DataOutputStream out, int value) throws IOException {
        int v = value;
        while ((v & ~0x7F) != 0) {
            out.writeByte((v & 0x7F) | 0x80);
            v >>>= 7;
        }
        out.writeByte(v);
    }

    static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        int pos = 0;
        while (true) {
            int current = in.readUnsignedByte();
            value |= (current & 0x7F) << pos;
            if ((current & 0x80) == 0) {
                return value;
            }
            pos += 7;
            if (pos >= 32) {
                throw new IOException("VarInt longo demais");
            }
        }
    }
}
