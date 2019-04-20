package org.devocative.sbis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

public class SearchDTO {

	@Getter
	@Setter
	@ToString
	@Accessors(chain = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class InfoRs {
		private String version;
	}

	@Getter
	@Setter
	@ToString
	@Accessors(chain = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Result<T> {
		private Long count;
		private List<T> result;
	}

	@Getter
	@Setter
	@ToString(of = {"nconst", "primaryName"})
	//@Accessors(chain = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class NameRs {
		private String nconst;
		private String primaryName;
		private Integer birthYear;
		private Integer deathYear;
		private String primaryProfession;
	}

	@Getter
	@Setter
	@ToString(of = {"tconst", "primaryTitle"})
	//@Accessors(chain = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TitleRs {
		private String tconst;
		private String titleType;
		private String primaryTitle;
		private String originalTitle;
		private Integer startYear;
		private Integer endYear;
		private String genres;

	}
}
