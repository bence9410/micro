package hu.beni.amusementpark.repository.custom.impl;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkPageResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.AmusementParkKnowVisitor;
import hu.beni.amusementpark.entity.AmusementParkKnowVisitor_;
import hu.beni.amusementpark.entity.AmusementPark_;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.GuestBookRegistry_;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Machine_;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.entity.Visitor_;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AmusementParkRepositoryCustomImpl implements AmusementParkRepositoryCustom {

	private final EntityManager entityManager;

	@Override
	public Page<AmusementParkPageResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		Long count = executeCountQuery(cb, dto);

		List<AmusementParkPageResponseDto> result = executeSearchQuery(cb, dto, pageable);

		return new PageImpl<>(result, pageable, count);
	}

	private Predicate[] createPredicates(CriteriaBuilder cb, Root<AmusementPark> root,
			AmusementParkSearchRequestDto dto) {
		List<Predicate> predicates = new ArrayList<>();

		if (dto != null) {
			ofNullable(dto.getName()).map(name -> cb.like(root.get(AmusementPark_.name), "%" + name + "%"))
					.ifPresent(predicates::add);

			ofNullable(dto.getCapitalMin()).map(capitalMin -> cb.ge(root.get(AmusementPark_.capital), capitalMin))
					.ifPresent(predicates::add);
			ofNullable(dto.getCapitalMax()).map(capitalMax -> cb.le(root.get(AmusementPark_.capital), capitalMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getTotalAreaMin())
					.map(totalAreaMin -> cb.ge(root.get(AmusementPark_.totalArea), totalAreaMin))
					.ifPresent(predicates::add);
			ofNullable(dto.getTotalAreaMax())
					.map(totalAreaMax -> cb.le(root.get(AmusementPark_.totalArea), totalAreaMax))
					.ifPresent(predicates::add);

			ofNullable(dto.getEntranceFeeMin())
					.map(entranceFeeMin -> cb.ge(root.get(AmusementPark_.entranceFee), entranceFeeMin))
					.ifPresent(predicates::add);
			ofNullable(dto.getEntranceFeeMax())
					.map(entranceFeeMax -> cb.le(root.get(AmusementPark_.entranceFee), entranceFeeMax))
					.ifPresent(predicates::add);
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	private Long executeCountQuery(CriteriaBuilder cb, AmusementParkSearchRequestDto dto) {
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AmusementPark> root = cq.from(AmusementPark.class);
		return entityManager
				.createQuery(cq.select(cb.count(root.get(AmusementPark_.id))).where(createPredicates(cb, root, dto)))
				.getSingleResult();
	}

	private List<AmusementParkPageResponseDto> executeSearchQuery(CriteriaBuilder cb, AmusementParkSearchRequestDto dto,
			Pageable pageable) {
		CriteriaQuery<AmusementParkPageResponseDto> cq = cb.createQuery(AmusementParkPageResponseDto.class);
		Root<AmusementPark> root = cq.from(AmusementPark.class);

		Subquery<Long> countMachines = cq.subquery(Long.class);
		Root<Machine> machineRoot = countMachines.from(Machine.class);
		countMachines.where(cb.equal(root, machineRoot.get(Machine_.amusementPark)));
		countMachines.select(cb.count(machineRoot));

		Subquery<Long> countGuestBooks = cq.subquery(Long.class);
		Root<GuestBookRegistry> guestBookRoot = countGuestBooks.from(GuestBookRegistry.class);
		countGuestBooks.where(cb.equal(root, guestBookRoot.get(GuestBookRegistry_.amusementPark)));
		countGuestBooks.select(cb.count(guestBookRoot));

		Subquery<Long> countActiveVisitors = cq.subquery(Long.class);
		Root<Visitor> activeRoot = countActiveVisitors.from(Visitor.class);
		countActiveVisitors.where(cb.equal(root, activeRoot.get(Visitor_.amusementPark)));
		countActiveVisitors.select(cb.count(activeRoot));

		Subquery<Long> countKnownVisitors = cq.subquery(Long.class);
		Root<AmusementParkKnowVisitor> knownRoot = countKnownVisitors.from(AmusementParkKnowVisitor.class);
		countKnownVisitors.where(cb.equal(root, knownRoot.get(AmusementParkKnowVisitor_.amusementPark)));
		countKnownVisitors.select(cb.count(knownRoot));

		cq.multiselect(root.get(AmusementPark_.id), root.get(AmusementPark_.name), root.get(AmusementPark_.capital),
				root.get(AmusementPark_.totalArea), root.get(AmusementPark_.entranceFee), countMachines.getSelection(),
				countGuestBooks.getSelection(), countActiveVisitors.getSelection(), countKnownVisitors.getSelection())
				.where(createPredicates(cb, root, dto));
		return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
	}

}
