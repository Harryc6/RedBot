# RedBot
A new project for the Night City Overdrive Discord server.

Please set the environmental variables below at runtime:

    DB_CONNECTION
    DB_DATABASE
    DB_HOST
    DB_PASSWORD
    DB_PORT
    DB_USERNAME
    DISCORD_TOKEN 
    DOCUMENTATION_URL

Table SQL:

    create table nco_pc
    (
    discord_name          varchar(32),
    character_name        varchar(32) not null
    constraint nco_pc_pk
    primary key,
    role                  varchar(32),
    role_rank             integer     default 4,
    creation_rank         varchar(32),
    rank                  varchar(32),
    street_cred           integer,
    bank                  integer,
    improvement_points    integer     default 0,
    reputation            integer     default 0,
    fame                  integer     default 0,
    vault                 varchar(32),
    downtime              integer     default 12,
    downtime_spent        integer     default 0,
    lifestyle             varchar(32) default 'Starter Lifestyle'::character varying,
    rent                  varchar(32) default 'Starter Rent'::character varying,
    games_overall         integer     default 0,
    weekly_games          integer     default 0,
    job_ban_until         date,
    retired_yn            varchar(1)  default 'n'::character varying,
    head_sp               integer,
    body_sp               integer,
    monthly_debt          integer     default 0,
    monthly_due           integer     default 5,
    intelligence          integer,
    reflexes              integer,
    dexterity             integer,
    technique             integer,
    cool                  integer,
    willpower             integer,
    luck                  integer,
    usable_luck           integer,
    movement              integer,
    body                  integer,
    current_empathy       integer,
    max_empathy           integer,
    current_hp            integer,
    max_hp                integer,
    current_humanity      integer,
    max_humanity          integer,
    a_v_tech              integer     default 0,
    accounting            integer     default 0,
    acting                integer     default 0,
    animal_handling       integer     default 0,
    archery               integer     default 0,
    athletics             integer     default 0,
    autofire              integer     default 0,
    basic_tech            integer     default 0,
    brawling              integer     default 0,
    bribery               integer     default 0,
    bureaucracy           integer     default 0,
    business              integer     default 0,
    composition           integer     default 0,
    conceal_reveal        integer     default 0,
    concentration         integer     default 0,
    contortionist         integer     default 0,
    conversation          integer     default 0,
    criminology           integer     default 0,
    cryptography          integer     default 0,
    cybertech             integer     default 0,
    dance                 integer     default 0,
    deduction             integer     default 0,
    demolitions           integer     default 0,
    drive_land            integer     default 0,
    education             integer     default 0,
    electronics_security  integer     default 0,
    endurance             integer     default 0,
    evasion               integer     default 0,
    first_aid             integer     default 0,
    forgery               integer     default 0,
    gamble                integer     default 0,
    handgun               integer     default 0,
    heavy_arms            integer     default 0,
    human_perception      integer     default 0,
    instrument_a          integer     default 0,
    instrument_b          integer     default 0,
    interrogation         integer     default 0,
    l_v_tech              integer     default 0,
    language_a            integer     default 0,
    language_b            integer     default 0,
    library_search        integer     default 0,
    lip_reading           integer     default 0,
    local_expert_a        integer     default 0,
    local_expert_b        integer     default 0,
    martial               integer     default 0,
    melee                 integer     default 0,
    paint_draw_sculpt     integer     default 0,
    paramedic             integer     default 0,
    perception            integer     default 0,
    personal_grooming     integer     default 0,
    persuasion            integer     default 0,
    photography_film      integer     default 0,
    pick_lock             integer     default 0,
    pick_pocket           integer     default 0,
    pilot_air             integer     default 0,
    pilot_sea             integer     default 0,
    riding                integer     default 0,
    s_v_tech              integer     default 0,
    science_a             integer     default 0,
    science_b             integer     default 0,
    shoulder_arms         integer     default 0,
    stealth               integer     default 0,
    streetslang           integer     default 0,
    streetwise            integer     default 0,
    tactics               integer     default 0,
    torture_drugs         integer     default 0,
    tracking              integer     default 0,
    trading               integer     default 0,
    wardrobe              integer     default 0,
    weaponstech           integer     default 0,
    wilderness            integer     default 0,
    your_home             integer     default 0,
    surgery               integer,
    pharmaceuticals       integer,
    cryosystem_operation  integer,
    field_expertise       integer,
    upgrade_expertise     integer,
    fabrication_expertise integer,
    invention_expertise   integer,
    created_on            timestamp   default CURRENT_TIMESTAMP,
    created_by            varchar(32)
    );

    create table nco_logging
    (
    id         bigserial not null,
    command    varchar,
    arg_0      varchar,
    arg_1      varchar,
    arg_2      varchar,
    arg_3      varchar,
    arg_4      varchar,
    arg_5      varchar,
    arg_6      varchar,
    arg_7      varchar,
    arg_8      varchar,
    created_on timestamp default CURRENT_TIMESTAMP,
    created_by varchar(32),
    arg_9      varchar
    );

    create table nco_fame
    (
    id             bigserial not null,
    character_name varchar,
    reason         varchar,
    fame           integer,
    created_on     timestamp default CURRENT_TIMESTAMP,
    created_by     varchar(32)
    );

    create table nco_gig
    (
    id           bigserial not null,
    gig_name     varchar,
    referee      varchar,
    completed_yn varchar   default 'n'::character varying,
    completed_on timestamp,
    created_on   timestamp default CURRENT_TIMESTAMP,
    created_by   varchar
    );