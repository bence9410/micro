package hu.beni.amusementpark.repository.custom.impl;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark_;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Machine_;
import hu.beni.amusementpark.repository.custom.MachineRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MachineRepositoryCustomImpl implements MachineRepositoryCustom {

	private final EntityManager entityManager;

	@Override
	public Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		Long count = executeCountQuery(cb, dto);

		List<MachineSearchResponseDto> result = executeSearchQuery(cb, dto, pageable);

		return new PageImpl<>(result, pageable, count);
	}

	private Predicate[] createPredicates(CriteriaBuilder cb, Root<Machine> root, MachineSearchRequestDto dto) {
		List<Predicate> predicates = new ArrayList<>();

		if (dto != null) {
			ofNullable(dto.getAmusementParkId()).map(amusementParkId -> cb
					.equal(root.get(Machine_.amusementPark).get(AmusementPark_.id), amusementParkId))
					.ifPresent(predicates::add);

			ofNullable(dto.getFantasyName())
					.map(fantasyName -> cb.like(root.get(Machine_.fantasyName), "%" + fantasyName + "%"))
					.ifPresent(predicates::add);

			ofNullable(dto.getSizeMin()).map(sizeMin -> cb.ge(root.get(Machine_.size), sizeMin))
					.ifPresent(predicates::add);

			ofNullable(dto.getSizeMax()).map(sizeMax -> cb.le(root.get(Machine_.size), sizeMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getPriceMin()).map(priceMin -> cb.ge(root.get(Machine_.price), priceMin))
					.ifPresent(predicates::add);

			ofNullable(dto.getPriceMax()).map(priceMax -> cb.le(root.get(Machine_.price), priceMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getNumberOfSeatsMin())
					.map(numberOfSeatsMin -> cb.ge(root.get(Machine_.numberOfSeats), numberOfSeatsMin))
					.ifPresent(predicates::add);

			ofNullable(dto.getNumberOfSeatsMax())
					.map(numberOfSeatsMax -> cb.le(root.get(Machine_.numberOfSeats), numberOfSeatsMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getMinimumRequiredAgeMin())
					.map(minimumRequiredAgeMin -> cb.ge(root.get(Machine_.minimumRequiredAge), minimumRequiredAgeMin))
					.ifPresent(predicates::add);

			ofNullable(dto.getMinimumRequiredAgeMax())
					.map(minimumRequiredAgeMax -> cb.le(root.get(Machine_.minimumRequiredAge), minimumRequiredAgeMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getTicketPriceMin())
					.map(ticketPriceMin -> cb.ge(root.get(Machine_.ticketPrice), ticketPriceMin))
					.ifPresent(predicates::add);

			ofNullable(dto.getTicketPriceMax())
					.map(ticketPriceMax -> cb.le(root.get(Machine_.ticketPrice), ticketPriceMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getType()).map(type -> cb.equal(root.get(Machine_.type), type)).ifPresent(predicates::add);

		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private Long executeCountQuery(CriteriaBuilder cb, MachineSearchRequestDto dto) {
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Machine> root = cq.from(Machine.class);
		return entityManager
				.createQuery(cq.select(cb.count(root.get(Machine_.id))).where(createPredicates(cb, root, dto)))
				.getSingleResult();
	}

	private List<MachineSearchResponseDto> executeSearchQuery(CriteriaBuilder cb, MachineSearchRequestDto dto,
			Pageable pageable) {
		CriteriaQuery<MachineSearchResponseDto> cq = cb.createQuery(MachineSearchResponseDto.class);
		Root<Machine> root = cq.from(Machine.class);

		cq.multiselect(root.get(Machine_.id), root.get(Machine_.fantasyName), root.get(Machine_.size),
				root.get(Machine_.price), root.get(Machine_.numberOfSeats), root.get(Machine_.minimumRequiredAge),
				root.get(Machine_.ticketPrice), root.get(Machine_.type)).where(createPredicates(cb, root, dto));
		return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
	}

}
