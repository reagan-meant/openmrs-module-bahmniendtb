SELECT
  pi.identifier,
  pn.given_name,
  pn.family_name,
  p.gender,
  p.birthdate,
  p.death_date,
  p.cause_of_death
FROM  person p
       INNER JOIN person_name pn ON p.person_id = pn.person_id
       INNER JOIN patient_identifier pi ON p.person_id = pi.patient_id
