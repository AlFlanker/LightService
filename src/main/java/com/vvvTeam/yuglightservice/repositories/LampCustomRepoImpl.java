package com.vvvTeam.yuglightservice.repositories;

import com.vvvTeam.yuglightservice.repositories.customsInterface.LampCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Repository
public class LampCustomRepoImpl implements LampCustomRepository {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final RowMapper<String> namesMapper = (ResultSet resultSet, int i) -> resultSet.getString("object_name");
    private final Pattern pattern = Pattern.compile("^(([0-9a-fA-F]{2})-){7}([0-9a-fA-F]{2})$");
    @Override
    public List<String> getExistLamp(Set<String> lamps, boolean deleted) {
        if(!checkData(lamps)) return Collections.emptyList();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("names", lamps).addValue("del",deleted);
        final String query = "select object_name from lamp where lamp.object_name in (:names) and lamp.is_deleted = :del";
        return jdbcOperations.query(query, sqlParameterSource, namesMapper);
    }
    private boolean checkData(Set<String> names) {
        for(String eui:names){ if (!pattern.matcher(eui).matches()) return false;}
        return true;
    }
}
