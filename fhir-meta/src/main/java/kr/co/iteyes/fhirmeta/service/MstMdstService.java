package kr.co.iteyes.fhirmeta.service;

import kr.co.iteyes.fhirmeta.dto.OrganizationSeedDto;
import kr.co.iteyes.fhirmeta.entity.MstMdst;
import kr.co.iteyes.fhirmeta.repository.MstMdstRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MstMdstService {

    private final MstMdstRepository mstMdstRepository;

    public MstMdst createMstMdst(OrganizationSeedDto.IssuRequest issuRequest) {
        // 의료기관명
        String provideInstitutionName = issuRequest.getProvideInstitutionName();
        if (provideInstitutionName == null)
            provideInstitutionName = issuRequest.getCareInstitutionSign();

        MstMdst mstMdst = MstMdst.builder()
                .cisn(issuRequest.getCareInstitutionSign())
                .pnstNo(issuRequest.getProvideInstitutionNo())
                .careInstNm(provideInstitutionName)
                .emrDvcd("unknown")
                .build();

        mstMdst = mstMdstRepository.save(mstMdst);
        return mstMdst;
    }
}
