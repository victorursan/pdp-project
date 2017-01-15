package com.pdp.opencl.utils;

import org.jocl.*;

import java.util.Arrays;

import static org.jocl.CL.*;
import static org.jocl.CL.clCreateContext;

/**
 * Created by dana on 1/8/17.
 */
public class JOCLUtils {
    public static cl_platform_id getPlatform() {
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        return platforms[platformIndex];
    }

    public static cl_device_id getDevice(cl_platform_id platform) {
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        return devices[deviceIndex];
    }

    public static cl_context getContext(cl_device_id device, cl_platform_id platform) {
        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        return clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);
    }

    public static void OCLclear(cl_mem[] memObjects, cl_kernel kernel, cl_program program, cl_command_queue commandQueue, cl_context context) {
        for (cl_mem m:memObjects) {
            clReleaseMemObject(m);
        }
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

}
