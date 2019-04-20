package org.devocative.sbis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.devocative.sbis.dto.SearchDTO;
import org.devocative.sbis.iservice.ISearchService;
import org.devocative.thallo.core.annotation.LogIt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@LogIt
@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService implements ISearchService {

	private final SparkSession session;

	@Value("${app.data.dir}")
	private String dataDir;

	private boolean vCommonLoaded = false;

	// ------------------------------

	@PostConstruct
	public void init() {
		final String dataDirPath = new File(dataDir).getAbsolutePath();

		log.info("SparkService: version=[{}], dataDir=[{}], dataDirPath=[{}]",
			session.version(), dataDir, dataDirPath);

		// https://github.com/apache/spark/blob/v2.4.1/sql/core/src/main/scala/org/apache/spark/sql/execution/datasources/csv/CSVOptions.scala#L74

		final Dataset<Row> nameDF = session
			.read()
			.option("inferSchema", "true")
			.option("header", "true")
			.option("delimiter", "\t")
			.option("nullValue", "\\N")
			.csv(dataDirPath + "/name.basics.tsv.gz");
		nameDF.createOrReplaceTempView("t_name");
		log.info("*** Name Schema: {}", nameDF.schema().json());

		final Dataset<Row> titleDF = session
			.read()
			.option("inferSchema", "true")
			.option("header", "true")
			.option("delimiter", "\t")
			.option("nullValue", "\\N")
			.csv(dataDirPath + "/title.basics.tsv.gz");
		titleDF.createOrReplaceTempView("t_title");
		log.info("*** Title Schema: {}", titleDF.schema().json());

		final Dataset<Row> titleNameDF = session
			.read()
			.option("inferSchema", "true")
			.option("header", "true")
			.option("delimiter", "\t")
			.option("nullValue", "\\N")
			.csv(dataDirPath + "/title.principals.tsv.gz");
		titleNameDF.createOrReplaceTempView("t_title_name");
		log.info("*** TitleName Schema: {}", titleNameDF.schema().json());
	}

	// ---------------

	@Override
	public SearchDTO.InfoRs info() {
		return new SearchDTO.InfoRs().setVersion(session.version());
	}

	@Override
	public SearchDTO.Result<SearchDTO.NameRs> searchActor(String name) {
		final Dataset<Row> rows = session.sql(String.format(
			"select * from t_name where primaryName like '%%%s%%'", name));

		final long count = rows.count();
		log.info("*** SearchActor: name=[{}] count=[{}]", name, count);

		final List<SearchDTO.NameRs> result = rows.limit(10)
			.as(Encoders.bean(SearchDTO.NameRs.class))
			.collectAsList();

		return new SearchDTO.Result<SearchDTO.NameRs>()
			.setCount(count)
			.setResult(result);

	}

	@Override
	public SearchDTO.Result<SearchDTO.TitleRs> searchTitle(String title) {
		final Dataset<Row> rows = session.sql(String.format(
			"select * from t_title " +
				"where primaryTitle like '%%%s%%' " +
				"and titleType = 'movie' " +
				"order by startYear desc", title));

		final long count = rows.count();
		log.info("*** SearchTitle: name=[{}] count=[{}]", title, count);

		final List<SearchDTO.TitleRs> result = rows.limit(10)
			.as(Encoders.bean(SearchDTO.TitleRs.class))
			.collectAsList();

		return new SearchDTO.Result<SearchDTO.TitleRs>()
			.setCount(count)
			.setResult(result);
	}

	@Override
	public SearchDTO.Result<SearchDTO.TitleRs> searchCommonTitle(List<String> actors) {
		loadVCommon();

		StringBuilder builder = new StringBuilder();
		builder
			.append("select tt.* ")
			.append("from t_title tt ")
			.append("join v_common vc on vc.tconst = tt.tconst ")
			.append("where tt.titleType = 'movie' ");
		for (String actor : actors) {
			builder.append(String.format("and array_contains(vc.names_arr, '%s') ", actor));
		}
		final Dataset<Row> rows = session.sql(builder.toString());

		final long count = rows.count();
		log.info("*** SearchCommonTitle: actors=[{}] count=[{}]", actors, count);

		final List<SearchDTO.TitleRs> result = rows.limit(10)
			.as(Encoders.bean(SearchDTO.TitleRs.class))
			.collectAsList();

		return new SearchDTO.Result<SearchDTO.TitleRs>()
			.setCount(count)
			.setResult(result);
	}

	// ------------------------------

	private synchronized void loadVCommon() {
		if (!vCommonLoaded) {
			final Dataset<Row> commonTitleDF = session.sql(
				"select ttn.tconst, collect_list(tn.primaryName) as names_arr " +
					"from t_title_name ttn " +
					"join t_name tn on tn.nconst = ttn.nconst " +
					"group by ttn.tconst");
			commonTitleDF.createOrReplaceTempView("v_common");
			log.info("*** Movie-by-Actors Schema: {}", commonTitleDF.schema().json());

			vCommonLoaded = true;
		}
	}
}
