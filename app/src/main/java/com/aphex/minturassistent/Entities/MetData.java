package com.aphex.minturassistent.Entities;

public final class MetData {
    public final String type;
    public final Geometry geometry;
    public final Properties properties;

    public double airTempValue;
    public String symbolCodeValue;

    public MetData(String type, Geometry geometry, Properties properties) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }

//    public double getAirTempValue() {
//        Properties.Timeseries.Data.Instant.Details
//    }


    public static final class Geometry {
        public final String type;
        public final double[] coordinates;

        public Geometry(String type, double[] coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }
    }

    public static final class Properties {
        public final Meta meta;
        public final Timeseries[] timeseries;

        public Properties(Meta meta, Timeseries[] timeseries) {
            this.meta = meta;
            this.timeseries = timeseries;

        }

        public static final class Meta {
            public final String updated_at;
            public final Units units;

            public Meta(String updated_at, Units units) {
                this.updated_at = updated_at;
                this.units = units;
            }

            public static final class Units {
                public final String air_pressure_at_sea_level;
                public final String air_temperature;
                public final String cloud_area_fraction;
                public final String precipitation_amount;
                public final String relative_humidity;
                public final String wind_from_direction;
                public final String wind_speed;

                public Units(String air_pressure_at_sea_level, String air_temperature, String cloud_area_fraction, String precipitation_amount, String relative_humidity, String wind_from_direction, String wind_speed) {
                    this.air_pressure_at_sea_level = air_pressure_at_sea_level;
                    this.air_temperature = air_temperature;
                    this.cloud_area_fraction = cloud_area_fraction;
                    this.precipitation_amount = precipitation_amount;
                    this.relative_humidity = relative_humidity;
                    this.wind_from_direction = wind_from_direction;
                    this.wind_speed = wind_speed;
                }
            }
        }

        public static final class Timeseries {
            public final String time;
            public final Data data;

            public Timeseries(String time, Data data) {
                this.time = time;
                this.data = data;

            }

            public Data getData() {
                return data;
            }

            public static final class Data {
                public final Instant instant;
                public final Next12hours next_12_hours;

                public Data(Instant instant, Next12hours next_12_hours) {
                    this.instant = instant;
                    this.next_12_hours = next_12_hours;
                }

                public Instant getInstant() {
                    return instant;
                }

                public Next12hours getNext_12_hours() {
                    return next_12_hours;
                }

                public static final class Instant {
                    public final Details details;

                    public Instant(Details details) {
                        this.details = details;
                    }

                    public Details getDetails() {
                        return details;
                    }

                    public static final class Details {
                        public final double air_pressure_at_sea_level;
                        public final double air_temperature;
                        public final double cloud_area_fraction;
                        public final double relative_humidity;
                        public final double wind_from_direction;
                        public final double wind_speed;
                        public double airTempValue;

                        public Details(double air_pressure_at_sea_level, double air_temperature, double cloud_area_fraction, double relative_humidity, double wind_from_direction, double wind_speed) {
                            this.air_pressure_at_sea_level = air_pressure_at_sea_level;
                            this.air_temperature = air_temperature;
                            this.cloud_area_fraction = cloud_area_fraction;
                            this.relative_humidity = relative_humidity;
                            this.wind_from_direction = wind_from_direction;
                            this.wind_speed = wind_speed;

                        }
                        public double getAirTempValue() {
                            airTempValue = getAir_temperature();
                            return airTempValue;
                        }
                        public double getAir_temperature() {
                            return air_temperature;
                        }
                    }
                }

                public static final class Next12hours {
                    public final Summary summary;

                    public Next12hours(Summary summary) {
                        this.summary = summary;
                    }

                    public Summary getSummary() {
                        return summary;
                    }

                    public static final class Summary {
                        public final String symbol_code;

                        public String getSymbol_code() {
                            return symbol_code;
                        }
                        public Summary(String symbol_code) {
                            this.symbol_code = symbol_code;
                        }
                    }
                }
            }
        }
    }
}