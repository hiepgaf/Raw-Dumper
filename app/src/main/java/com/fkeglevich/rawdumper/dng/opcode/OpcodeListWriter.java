/*
 * Copyright 2018, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.dng.opcode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static com.fkeglevich.rawdumper.util.DataSize.INT_SIZE;

/**
 * This class is used to write opcodelists DNG tags
 *
 * Created by Flávio Keglevich on 30/03/18.
 */

public class OpcodeListWriter
{
    public static byte[] toByteArray(List<Opcode> opcodes)
    {
        ByteBuffer buffer = ByteBuffer.allocate(calculateTotalSize(opcodes));
        buffer.order(ByteOrder.BIG_ENDIAN); //opcodelists are ALWAYS stored in big endian byte order

        buffer.putInt(opcodes.size());

        for (Opcode opcode : opcodes)
        {
            buffer.putInt(opcode.getId())
                  .put   (opcode.getDngVersion().asBytes())
                  .putInt(opcode.getFlags())
                  .putInt(opcode.getParameterAreaSize());

            opcode.writeOpcodeData(buffer);
        }

        return buffer.array();
    }

    private static int calculateTotalSize(List<Opcode> opcodes)
    {
        int totalSize = INT_SIZE;

        for (Opcode opcode : opcodes)
            totalSize += opcode.getOpcodeSize();

        return totalSize;
    }
}
