package org.devocative.sbis.iservice;

import org.devocative.sbis.dto.SearchDTO;

import java.util.List;

public interface ISearchService {
	SearchDTO.InfoRs info();

	SearchDTO.Result<SearchDTO.NameRs> searchActor(String name);

	SearchDTO.Result<SearchDTO.TitleRs> searchTitle(String title);

	SearchDTO.Result<SearchDTO.TitleRs> searchCommonTitle(List<String> actors);
}