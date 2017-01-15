package com.pdp.opencl.models;

import com.pdp.opencl.utils.JOCLUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jocl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.pdp.opencl.utils.IOUtils.readFile;
import static com.pdp.opencl.utils.JOCLUtils.*;
import static java.util.stream.Collectors.toList;
import static org.jocl.CL.*;

public final class Board implements Comparable {
    final private int[] pieces;
    final private Board parent;
    final private boolean solution;

    public Board(final int[] pieces) {
        this.pieces = pieces;
        this.parent = null;
        this.solution = checkSolution();
    }

    private Board(final int[] pieces, final Board parent) {
        this.pieces = pieces;
        this.parent = parent;
        this.solution = checkSolution();
    }

    public Board getParent() {
        return parent;
    }

    public int[] getPieces() {
        return pieces;
    }

    private Board move(int direction) {
        // 1 is up, 2 is right, 3 is down, 4 is left (clockwise)
        final Integer emptyPosition = getEmptyPiece();
        int sa = pieces.length;
        int[] dstArray = new int[sa];
        Pointer dst = Pointer.to(dstArray);
        Pointer srcA = Pointer.to(pieces);

        String programSource = readFile("/home/dana/PP_Proj/pdp-project/opencl/src/main/java/com/pdp/opencl/models/oclfiles/move.cl");

        cl_platform_id platform = getPlatform();
        cl_device_id device = getDevice(platform);
        cl_context context = getContext(device, platform);
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);

        cl_mem memObjects[] = new cl_mem[2];
        memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * sa, srcA, null);
        memObjects[1] = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_int * sa, null, null);

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "move", null);

        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, Sizeof.cl_int, Pointer.to(new int[]{sa}));
        clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{emptyPosition}));
        clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[]{direction}));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{sa};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[1], CL_TRUE, 0, Sizeof.cl_int * sa, dst, 0, null, null);

        // Release kernel, program, and memory objects
        OCLclear(memObjects, kernel, program, commandQueue, context);
        if(dstArray[0] >= 0) {
            return new Board(dstArray);
        } else {
            return new Board(new int[]{});
        }
    }

    public List<Board> moveList() {
        return Stream.of(1, 2, 3, 4).map(this::move).collect(toList());
    }

    private boolean checkSolution() {
        int sa = pieces.length;
        if(sa < 16) return false;
        int dstArray[] = new int[1];
        String programSource =
                readFile("/home/dana/PP_Proj/pdp-project/opencl/src/main/java/com/pdp/opencl/models/oclfiles/check.cl");

        Pointer srcA = Pointer.to(pieces);
        Pointer dst = Pointer.to(dstArray);

        cl_platform_id platform = getPlatform();
        cl_device_id device = getDevice(platform);
        cl_context context = getContext(device, platform);
        cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);
        cl_mem memObjects[] = new cl_mem[2];
        memObjects[0] = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_int * sa, srcA, null);
        memObjects[1] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_int, null, null);

        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "sorted", null);

        // Set the arguments for the kernel
        clSetKernelArg(kernel, 0,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, 1,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, 2, Sizeof.cl_int, Pointer.to(new int[]{pieces.length}));

        // Set the work-item dimensions
        long global_work_size[] = new long[]{1};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[1], CL_TRUE, 0, Sizeof.cl_int, dst, 0, null, null);

        // Release kernel, program, and memory objects
        OCLclear(memObjects, kernel, program, commandQueue, context);

        return dstArray[0] != 0;
    }

    private int getEmptyPiece() {
        for (int i = 0; i < pieces.length; i++)
            if (pieces[i] == 16)
                return i;
        return -1;
    }

    public void toStr() {
        if(pieces.length == 16) {
            System.out.print("\n");
            System.out.print("+----+----+");
            for (int y = 0; y < pieces.length; y++) {
                if (y % 4 == 0)
                    System.out.print(" \n");
                System.out.print(pieces[y]);
                System.out.print(" ");
            }
            System.out.print("\n+----+----+");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return new EqualsBuilder()
                .append(pieces, board.pieces)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(pieces)
                .toHashCode();
    }

    @Override
    public int compareTo(final Object o) {
        if (this == o) return 0;

        if (o == null || getClass() != o.getClass()) return -1;

        Board board = (Board) o;
        if (board.pieces.equals(this.pieces)) {
            return 0;
        }
        return 1;
    }
}