package kr.co.iteyes.fhirmeta.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PhrDbValLogWrapperDto {

    private List<PhrDbValLogDto> logs;
}

