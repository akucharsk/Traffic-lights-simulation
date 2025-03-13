package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String inputFile = args[0];
            String outputFile = args[1];
            Main.runOffline();
        } else {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(10000, InetAddress.getByName("localhost"));
                System.out.println("Socket starting");
                socket.setReuseAddress(true);

                ObjectMapper mapper = new ObjectMapper();
                SimpleModule module = new SimpleModule("JunctionSerializer");
                module.addSerializer(Junction.class, new JunctionSerializer(Junction.class));
                mapper.registerModule(module);

                final int msgSize = 4096;
                Junction junction = new Junction();
                while (true) {
                    DatagramPacket recv = new DatagramPacket(new byte[msgSize], msgSize);
                    socket.receive(recv);
                    String data = new String(recv.getData(), StandardCharsets.UTF_8);

                    Command cmd = null;
                    System.out.println(data);
                    if (data.startsWith("VADD"))
                        cmd = VehicleAddCommand.parse(data);
                    else if (data.startsWith("STEP"))
                        cmd = new StepCommand();
                    else if (data.startsWith("STOP"))
                        break;

                    if (cmd != null){
                        cmd.setJunction(junction);
                        cmd.execute();

                        byte[] buf = mapper.writeValueAsString(junction).getBytes(StandardCharsets.UTF_8);
                        DatagramPacket send = new DatagramPacket(buf, buf.length, recv.getAddress(), recv.getPort());
                        socket.send(send);
                    } else {
                        byte[] msg = "ok".getBytes(StandardCharsets.UTF_8);
                        DatagramPacket send = new DatagramPacket(msg, msg.length, recv.getAddress(), recv.getPort());
                        socket.send(send);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }

    private static void runOnline() {

    }

    private static void runOffline() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule commandDeserializer = new SimpleModule("CommandDeserializer");
        commandDeserializer.addDeserializer(Program.class, new ProgramDeserializer(Program.class));
        mapper.registerModule(commandDeserializer);

        SimpleModule statusSerializer = new SimpleModule("StatusSerializer");
        statusSerializer.addSerializer(StepStatus.class, new StepStatusSerializer(StepStatus.class));
        mapper.registerModule(statusSerializer);

        Program program = mapper.readValue(new File("input.json"), Program.class);
        program.execute();

        StepStatus status = new StepStatus(StepCommand.getDepartedVehicles());
        String serialized = mapper.writeValueAsString(status);

        BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));
        writer.write(serialized);
        writer.close();
    }
}