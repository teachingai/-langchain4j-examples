package com.github.hiwepy.vertexai.aisql;

import java.util.List;
import java.util.Map;

public record Answer(String sqlQuery, List<Map<String, Object>> results) { }
