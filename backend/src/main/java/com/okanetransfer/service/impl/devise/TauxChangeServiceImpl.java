package com.okanetransfer.service.impl.devise;

import com.okanetransfer.entity.devise.Devise;
import com.okanetransfer.entity.devise.HistoriqueTaux;
import com.okanetransfer.repository.devise.DeviseRepository;
import com.okanetransfer.repository.devise.HistoriqueTauxRepository;
import com.okanetransfer.service.converter.devise.DeviseConverter;
import com.okanetransfer.service.dto.devise.external.ExchangeRateApiResponse;
import com.okanetransfer.service.dto.devise.response.DeviseResponse;
import com.okanetransfer.service.facade.devise.ITauxChangeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TauxChangeServiceImpl implements ITauxChangeService {

    private static final String API_URL = "https://open.er-api.com/v6/latest/EUR";
    private static final String SOURCE_API = "API";

    private final DeviseRepository deviseRepository;
    private final HistoriqueTauxRepository historiqueTauxRepository;
    private final DeviseConverter deviseConverter;
    private final RestTemplate restTemplate;

    public TauxChangeServiceImpl(DeviseRepository deviseRepository,
                                  HistoriqueTauxRepository historiqueTauxRepository,
                                  DeviseConverter deviseConverter,
                                  RestTemplate restTemplate) {
        this.deviseRepository = deviseRepository;
        this.historiqueTauxRepository = historiqueTauxRepository;
        this.deviseConverter = deviseConverter;
        this.restTemplate = restTemplate;
    }

    /** Mise à jour automatique quotidienne (03h00, heure du serveur). */
    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public List<DeviseResponse> mettreAJourTauxDepuisApi() {
        ExchangeRateApiResponse response = restTemplate.getForObject(API_URL, ExchangeRateApiResponse.class);

        if (response == null || !"success".equals(response.getResult()) || response.getRates() == null) {
            throw new IllegalStateException("Réponse invalide de l'API de taux de change.");
        }

        for (Devise devise : deviseRepository.findAll()) {
            BigDecimal eurVersDevise = response.getRates().get(devise.getCode());
            if (eurVersDevise == null || eurVersDevise.compareTo(BigDecimal.ZERO) == 0) {
                continue; // devise non couverte par l'API
            }

            BigDecimal nouveauTaux = BigDecimal.ONE.divide(eurVersDevise, 6, RoundingMode.HALF_UP);

            if (nouveauTaux.compareTo(devise.getTauxVersEuro()) != 0) {
                HistoriqueTaux historique = new HistoriqueTaux();
                historique.setDevise(devise);
                historique.setAncienTaux(devise.getTauxVersEuro());
                historique.setNouveauTaux(nouveauTaux);
                historique.setDate(LocalDateTime.now());
                historique.setSource(SOURCE_API);
                historiqueTauxRepository.save(historique);

                devise.setTauxVersEuro(nouveauTaux);
                devise.setSourceTaux(SOURCE_API);
                devise.setDerniereMaj(LocalDateTime.now());
                deviseRepository.save(devise);
            }
        }

        return deviseConverter.toResponseList(deviseRepository.findAll());
    }
}
