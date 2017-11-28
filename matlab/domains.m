clear

% Genius directory
genius_dir = '/home/juillermo/eclipse-workspace/The_nego_people/genius/';

%% ----   ANAC 2015   ---- 

%% Movie (bid space: 4) (need to delete ugly &)
profiles_dir = 'etc/templates/ANAC2015/group7-movie';
profile_name = 'movie-profile';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Car purchase (bid space: 216)
profiles_dir = 'etc/templates/ANAC2015/group11-car_purchase';
profile_name = 'car_purchase_util';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Car domain (bid space: 240)
profiles_dir = 'etc/templates/ANAC2015/group5-car_domain';
profile_name = 'car-Profile';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Sporthal (bid space: 243) (need to delete a weird character at the xml)
profiles_dir = 'etc/templates/ANAC2015/group2-new_sporthal';
profile_name = 'New_sporthal_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Zoning plan (bid space: 448) (need to delete ugly &)
profiles_dir = 'etc/templates/ANAC2015/group4-zoning_plan';
profile_name = 'Group4_util';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Building Construction (bid space: 864) (Only right from 11 to 19. Need to rename those to 1-9)
profiles_dir = 'etc/templates/ANAC2015/group10-building_construction';
profile_name = 'building_construction_util';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Holiday (bid space: 1024)
profiles_dir = 'etc/templates/ANAC2015/group8-holiday';
profile_name = 'holiday-Profile';
n_profiles = 9;

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Dinner profiles (bid space: 1200)
profiles_dir = 'etc/templates/ANAC2015/group2-dinner';
profile_name = 'Dinner_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% University profiles (bid space: 2250)
profiles_dir = 'etc/templates/ANAC2015/group1-university';
profile_name = 'University_util';
n_profiles = 9;

profile_nums = [1 4 7];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Symposium (bid space: 2304) (need to delete ugly &)
profiles_dir = 'etc/templates/ANAC2015/group12-symposium';
profile_name = 'Symposium-Profile';
n_profiles = 9;

profile_nums = [1 5 8];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)

%% Politics (bid space: 23040)
profiles_dir = 'etc/templates/ANAC2015/group2-politics';
profile_name = 'Politics_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Bank robbery (bid space: 18)
% profiles_dir = 'etc/templates/ANAC2015/group3-bank_robbery';
% profile_name = 'Bank_Robbery_util';
% n_profiles = 9;
% 
% profile_nums = [1 2 3];
% [umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)
% 
% allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)
% 
% %% Tram (bid space: 972)
% profiles_dir = 'etc/templates/ANAC2015/group6-tram';
% profile_name = 'Tram_Profile';
% n_profiles = 9;
% 
% profile_nums = [1 2 3];
% [umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)
% 
% allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)
% 
% %% Killer robot (bid space: 480) (Has continuous variables)
% profiles_dir = 'etc/templates/ANAC2015/group9-killer_robot';
% profile_name = 'KillerRobot_util';
% n_profiles = 5;
% 
% profile_nums = [1 2 3];
% [umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)
% 
% allCombinations(n_profiles, [genius_dir profiles_dir], profile_name)
% 
% %% Vacation (bid space: 400) (Has continous variables)
% profiles_dir = 'etc/templates/ANAC2015/group9-vacation';
% profile_name = 'Vacation_util';
% 
% profile_nums = [2 3 4];
% [umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% ----   ANAC2016   ----

%% Smartgrid of Agent Light (bid space: 625)

profiles_dir = 'etc/templates/ANAC2016/AgentLightSmartGrid';
profile_name = 'SmartEnergyGrid_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Smartgrid of Cadeceus (bid space: 80)

profiles_dir = 'etc/templates/ANAC2016/Caduceus';
profile_name = 'Smart_Grid_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Smartgrid of Maxoops (bid space: 7200)

profiles_dir = 'etc/templates/ANAC2016/Maxoops';
profile_name = 'WindFarm_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Smartgrid of parsAgent2 (bid space: 612)

profiles_dir = 'etc/templates/ANAC2016/parsAgent2';
profile_name = 'PEnergy_util';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)

%% Smartgrid of parsCat (bid space: 290625)

profiles_dir = 'etc/templates/ANAC2016/parsCat';
profile_name = 'profile';

profile_nums = [1 2 3];
[umax, umin, bid_space] = analyzeDomain([genius_dir profiles_dir], profile_name, profile_nums)
