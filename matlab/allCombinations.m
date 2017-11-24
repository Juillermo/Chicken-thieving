function allCombinations(n_profiles, profiles_dir, profile_name)
    profile_nums = [1 2 3];
    [umax, umin] = analyzeDomain(profiles_dir, profile_name, profile_nums);

    profile_nums = combinator(n_profiles, 3, 'c');
    n_comb = size(profile_nums, 1);

    umax_comb = zeros([size(umax) n_comb]);
    umin_comb = zeros([size(umin) n_comb]);
    for i = 1:n_comb
        [umax_comb(:,:,i), umin_comb(:,:,i)] = analyzeDomain(profiles_dir, profile_name, profile_nums(i,:));
    end

    figure(2), clf
    dist_to_nash = permute([umax_comb(5,1,:), umax_comb(5,2,:), umax_comb(5,3,:), umin_comb(5,1,:)], [3 2 1]);
    boxplot(dist_to_nash)
    dist_to_nash
    title('Distances to Nash of max utility points across all combinations')
    xlabel('Max util point of 1st, 2nd, 3rd agent, and symmetric point')
    ylabel('Distance to Nash')
    
    figure(3), clf
    nash_points = permute(umax_comb(1:3,4,:), [1 3 2]);
    scatter3(nash_points(1,:), nash_points(2,:), nash_points(3,:));
    title('All nash points')
    xlabel('First agent`s utility')
    ylabel('Second agent`s utility')
    zlabel('Third agent`s utility')
    axis([0 1 0 1 0 1])
end