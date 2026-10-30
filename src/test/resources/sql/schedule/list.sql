INSERT INTO groups (id, name)
VALUES
    ('66666666-6666-4666-6666-666666666666', 'Java group'),
    ('77777777-7777-4777-7777-777777777777', 'Spring group');

INSERT INTO teachers (id, first_name, last_name)
VALUES
    ('88888888-8888-4888-8888-888888888888', 'Андрей', 'Андреев'),
    ('99999999-9999-4999-9999-999999999999', 'Борис', 'Борисов');

INSERT INTO courses (id, name, description)
VALUES
    ('aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa', 'Java', 'Backend development with Java'),
    ('bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb', 'Spring', 'Backend development with Spring Framework');

INSERT INTO schedules (id, group_id, teacher_id, course_id, start_date, end_date)
VALUES
    (
        '11111111-1111-4111-1111-111111111111',
        '66666666-6666-4666-6666-666666666666',
        '88888888-8888-4888-8888-888888888888',
        'aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa',
        '2026-10-22 10:30:00',
        '2026-10-22 12:00:00'
    ),
    (
        '22222222-2222-4222-2222-222222222222',
        '66666666-6666-4666-6666-666666666666',
        '88888888-8888-4888-8888-888888888888',
        'aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa',
        '2026-10-22 13:30:00',
        '2026-10-22 15:00:00'
    ),
    (
        '33333333-3333-4333-3333-333333333333',
        '66666666-6666-4666-6666-666666666666',
        '99999999-9999-4999-9999-999999999999',
        'bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb',
        '2026-10-22 17:30:00',
        '2026-10-22 19:00:00'
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        '77777777-7777-4777-7777-777777777777',
        '99999999-9999-4999-9999-999999999999',
        'bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb',
        '2026-10-23 10:30:00',
        '2026-10-23 12:00:00'
    ),
    (
        '55555555-5555-4555-5555-555555555555',
        '77777777-7777-4777-7777-777777777777',
        '99999999-9999-4999-9999-999999999999',
        'bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb',
        '2026-10-23 13:30:00',
        '2026-10-23 15:00:00'
    );