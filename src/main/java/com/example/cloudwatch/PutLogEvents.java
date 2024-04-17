// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudwatch;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
// snippet-start:[cloudwatch.java2.put_log_events.main]
// snippet-start:[cloudwatch.java2.put_log_events.import]
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import java.util.Arrays;
import java.util.Scanner;
// snippet-end:[cloudwatch.java2.put_log_events.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutLogEvents {
    public static void main(String[] args) {
      

        if (args.length != 2) {
            System.out.println("usage: first argument is logs group name,"
            		+ " second argument is logs data stream name");
            
        } else {

            String logGroupName = args[0];
            String streamName = args[1];
            CloudWatchLogsClient logsClient = CloudWatchLogsClient.builder().region(Region.US_EAST_1)
                    .build();
            DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamNamePrefix(streamName)
                    .build();
         // Assume that a single stream is returned since a specific stream name was
            // specified in the previous request.
            
            DescribeLogStreamsResponse describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest);
            String sequenceToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();
            
            putCWLogEvents(logsClient, logGroupName, streamName, sequenceToken);
            logsClient.close();
        }

    }

    public static void putCWLogEvents(CloudWatchLogsClient logsClient, String logGroupName, String streamName,
    		String sequenceToken) {
    	Scanner scanner = new Scanner(System.in);
    	String line = "";
    	
            while (true) {
            	System.out.println("enter message (log as alarm should contain ERROR):");
            	line = scanner.nextLine();
				// Build an input log message to put to CloudWatch.
				InputLogEvent inputLogEvent = InputLogEvent.builder().message(line)
						.timestamp(System.currentTimeMillis()).build();
				// Specify the request parameters.
				// Sequence token is required so that the log can be written to the
				// latest location in the stream.
				PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
						.logEvents(Arrays.asList(inputLogEvent)).logGroupName(logGroupName).logStreamName(streamName)
						.sequenceToken(sequenceToken).build();
				logsClient.putLogEvents(putLogEventsRequest);
				//if no exception then everything is OK
			}


    }
}
