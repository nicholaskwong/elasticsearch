/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.prelert.job.results;

import org.elasticsearch.action.support.ToXContentToBytes;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.ParseFieldMatcherSupplier;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.XContentBuilder;
import java.io.IOException;
import java.util.Objects;

public class PartitionScore extends ToXContentToBytes implements Writeable {
    public static final ParseField PARTITION_SCORE = new ParseField("partition_score");

    private String partitionFieldValue;
    private String partitionFieldName;
    private double anomalyScore;
    private double probability;
    private boolean hadBigNormalisedUpdate;

    public static final ConstructingObjectParser<PartitionScore, ParseFieldMatcherSupplier> PARSER = new ConstructingObjectParser<>(
            PARTITION_SCORE.getPreferredName(), a -> new PartitionScore((String) a[0], (String) a[1], (Double) a[2], (Double) a[3]));

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), AnomalyRecord.PARTITION_FIELD_NAME);
        PARSER.declareString(ConstructingObjectParser.constructorArg(), AnomalyRecord.PARTITION_FIELD_VALUE);
        PARSER.declareDouble(ConstructingObjectParser.constructorArg(), AnomalyRecord.ANOMALY_SCORE);
        PARSER.declareDouble(ConstructingObjectParser.constructorArg(), AnomalyRecord.PROBABILITY);
    }

    public PartitionScore(String fieldName, String fieldValue, double anomalyScore, double probability) {
        hadBigNormalisedUpdate = false;
        partitionFieldName = fieldName;
        partitionFieldValue = fieldValue;
        this.anomalyScore = anomalyScore;
        this.probability = probability;
    }

    public PartitionScore(StreamInput in) throws IOException {
        partitionFieldName = in.readString();
        partitionFieldValue = in.readString();
        anomalyScore = in.readDouble();
        probability = in.readDouble();
        hadBigNormalisedUpdate = in.readBoolean();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(partitionFieldName);
        out.writeString(partitionFieldValue);
        out.writeDouble(anomalyScore);
        out.writeDouble(probability);
        out.writeBoolean(hadBigNormalisedUpdate);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(AnomalyRecord.PARTITION_FIELD_NAME.getPreferredName(), partitionFieldName);
        builder.field(AnomalyRecord.PARTITION_FIELD_VALUE.getPreferredName(), partitionFieldValue);
        builder.field(AnomalyRecord.ANOMALY_SCORE.getPreferredName(), anomalyScore);
        builder.field(AnomalyRecord.PROBABILITY.getPreferredName(), probability);
        builder.endObject();
        return builder;
    }

    public double getAnomalyScore() {
        return anomalyScore;
    }

    public void setAnomalyScore(double anomalyScore) {
        this.anomalyScore = anomalyScore;
    }

    public String getPartitionFieldName() {
        return partitionFieldName;
    }

    public void setPartitionFieldName(String partitionFieldName) {
        this.partitionFieldName = partitionFieldName;
    }

    public String getPartitionFieldValue() {
        return partitionFieldValue;
    }

    public void setPartitionFieldValue(String partitionFieldValue) {
        this.partitionFieldValue = partitionFieldValue;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partitionFieldName, partitionFieldValue, probability, anomalyScore);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof PartitionScore == false) {
            return false;
        }

        PartitionScore that = (PartitionScore) other;

        // hadBigNormalisedUpdate is deliberately excluded from the test
        // as is id, which is generated by the datastore
        return Objects.equals(this.partitionFieldValue, that.partitionFieldValue)
                && Objects.equals(this.partitionFieldName, that.partitionFieldName) && (this.probability == that.probability)
                && (this.anomalyScore == that.anomalyScore);
    }

    public boolean hadBigNormalisedUpdate() {
        return hadBigNormalisedUpdate;
    }

    public void resetBigNormalisedUpdateFlag() {
        hadBigNormalisedUpdate = false;
    }

    public void raiseBigNormalisedUpdateFlag() {
        hadBigNormalisedUpdate = true;
    }
}
