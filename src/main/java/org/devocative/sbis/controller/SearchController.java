package org.devocative.sbis.controller;

import lombok.RequiredArgsConstructor;
import org.devocative.sbis.dto.SearchDTO;
import org.devocative.sbis.iservice.ISearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SearchController {
	private final ISearchService sparkService;

	@GetMapping("/info")
	public SearchDTO.InfoRs info() {
		return sparkService.info();
	}

	@GetMapping("/actors/{name}")
	public SearchDTO.Result<SearchDTO.NameRs> searchActor(@PathVariable String name) {
		return sparkService.searchActor(name);
	}

	@GetMapping("/movies/{title}")
	public SearchDTO.Result<SearchDTO.TitleRs> searchTitle(@PathVariable String title) {
		return sparkService.searchTitle(title);
	}

	@PostMapping("/actors/common")
	public SearchDTO.Result<SearchDTO.TitleRs> searchCommonTitle(@RequestBody List<String> actors) {
		return sparkService.searchCommonTitle(actors);
	}

}