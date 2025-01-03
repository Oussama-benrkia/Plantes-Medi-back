package ma.m3achaba.plantes.services.imp;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.CommentaireRequest;
import ma.m3achaba.plantes.dto.CommentaireResponse;
import ma.m3achaba.plantes.mapper.CommentaireMapper;
import ma.m3achaba.plantes.model.*;
import ma.m3achaba.plantes.repo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentaireService {

    private static final String NOT_FOUND_MESSAGE = " not found";

    private final CommentairepltRepository commentairepltRepository;
    private final CommentaireMapper commentaireMapper;
    private final PlantesRepository plantesRepository;
    private final UserRepo userRepo;
    private final ArticleRepository articleRepository;
    private final CommentaireartRepository commentaireartRepository;

    public Optional<CommentaireResponse> savePlante(CommentaireRequest commentaireRequest, Long idPlante) {
        PlantComment cmt = commentaireMapper.toEntityPlante(commentaireRequest);
        Plantes pl = plantesRepository.findById(idPlante).orElseThrow(() -> new EntityNotFoundException("Plante " + idPlante + NOT_FOUND_MESSAGE));
        cmt.setPlantes(pl);
        User use = userRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("User" + NOT_FOUND_MESSAGE));
        cmt.setUtilisateur(use);
        return Optional.ofNullable(commentaireMapper.toResponse(commentairepltRepository.save(cmt)));
    }

    public PageResponse<CommentaireResponse> listPlante(Long idPlante, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Plantes pl = plantesRepository.findById(idPlante).orElseThrow(() -> new EntityNotFoundException("Plante " + idPlante + NOT_FOUND_MESSAGE));
        Page<PlantComment> commentaire = commentairepltRepository.findAllByPlantes(pl, pageable);
        return PageResponse.<CommentaireResponse>builder()
                .totalPages(commentaire.getTotalPages())
                .totalElements(commentaire.getTotalElements())
                .last(commentaire.isLast())
                .first(commentaire.isFirst())
                .number(commentaire.getNumber())
                .size(commentaire.getSize())
                .content(commentaire.getContent().stream().map(commentaireMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    public Optional<CommentaireResponse> saveArticle(CommentaireRequest commentaireRequest, Long idArticle) {
        ArticleComment cmt = commentaireMapper.toEntityArticle(commentaireRequest);
        Article article = articleRepository.findById(idArticle).orElseThrow(() -> new EntityNotFoundException("Article " + idArticle + NOT_FOUND_MESSAGE));
        cmt.setArticle(article);
        User user = userRepo.findById(1L).orElseThrow(() -> new EntityNotFoundException("User" + NOT_FOUND_MESSAGE));
        cmt.setUtilisateur(user);
        return Optional.ofNullable(commentaireMapper.toResponse(commentaireartRepository.save(cmt)));
    }

    public PageResponse<CommentaireResponse> listArticle(Long idArticle, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Article article = articleRepository.findById(idArticle).orElseThrow(() -> new EntityNotFoundException("Article " + idArticle + NOT_FOUND_MESSAGE));
        Page<ArticleComment> commentaires = commentaireartRepository.findAllByArticle(article, pageable);
        return PageResponse.<CommentaireResponse>builder()
                .totalPages(commentaires.getTotalPages())
                .totalElements(commentaires.getTotalElements())
                .last(commentaires.isLast())
                .first(commentaires.isFirst())
                .number(commentaires.getNumber())
                .size(commentaires.getSize())
                .content(commentaires.getContent().stream().map(commentaireMapper::toResponse).collect(Collectors.toList()))
                .build();
    }
}
