# install fastlane via bundle
# cd fastlane
# bundle config set path fastlane
# bundle install 

# run fastlane
# bundle exec fastlane 

# update fastlane
# bundle update fastlane

# update plugins
# bundle exec fastlane update_plugins

# Legacy versionCodes topped out below 340100050 under the old major/minor/patch/build
# encoding, so a naive YYMMDD-based versionCode would not be strictly increasing as the
# Play Store requires. This offset pushes every CalVer versionCode above that ceiling.
VERSION_CODE_OFFSET = 400000000
DATE_MULTIPLIER = 1000

TYPE_FINAL = "final"

BUNDLE_PATH = "app/build/outputs/bundle/gplayRelease/app-gplay-release.aab"

default_platform(:android)

ENV['SUPPLY_UPLOAD_MAX_RETRIES']='10'

platform :android do
    desc "Run tests and build app"
    lane :releasePhase1 do
    	sh("rm -rf ../release/ && mkdir -p ../release")
		gradle(task: 'clean')
        
        versionInfo = getVersionInfo()
        test() # needs to be overwritten in apps
        
        buildBundle()
        buildAPKs(code: versionInfo["versionCode"])
    end

    desc "Print version info"
    lane :printVersionInfo do
        versionInfo = getVersionInfo()
        versionComponents = parseVersionCode(versionInfo)
        print "Version code: #{versionInfo["versionCode"]}\n"
        print "Version name: #{versionInfo["versionName"]}\n"
        print "Year: #{versionComponents["year"]}\n"
        print "Month: #{versionComponents["month"]}\n"
        print "Day: #{versionComponents["day"]}\n"
        print "Id: #{versionComponents["id"]}\n"
    end

    # Usage: fastlane incrementVersion type:(release|build)
    # release: sets the date to today and resets id to 0
    # build: keeps today's date and increments id by 1 (or starts a new day at id 0)
    desc "Increment version code and version name"
    lane :incrementVersion do |options|
        versionInfo = getVersionInfo()
        versionComponents = parseVersionCode(versionInfo)
        newVersionComponents = incrementVersionComponents(versionComponents: versionComponents, type: options[:type])
        versionNameGenerated = generateVersionName(newVersionComponents)
        versionCodeGenerated = generateVersionCode(newVersionComponents)

        promptYesNo(text: "Version code: #{versionInfo["versionCode"]} -> #{versionCodeGenerated}\n" +
                        "Version name: #{versionInfo["versionName"]} -> #{versionNameGenerated}"
        )
        writeVersions(versionCode: versionCodeGenerated, versionName: versionNameGenerated)
    end
    
    desc "Upload Alpha version to play store"
	lane :uploadAlphaToPlayStore do |options|
		upload_to_play_store(
			skip_upload_images: true,
			skip_upload_aab: true,
			skip_upload_changelogs: true,
			skip_upload_metadata: true,
			skip_upload_screenshots: true,
			track: 'alpha',
			apk: "/home/androiddaily/apks-#{APP}/android-#{APP}-" + options[:version] + ".apk",
		)
	end

    desc "Parse year, month, day and id from versionCode"
    private_lane :parseVersionCode do |versionInfo|
        dateAndId = versionInfo["versionCode"] - VERSION_CODE_OFFSET
        id = dateAndId % DATE_MULTIPLIER
        date = dateAndId / DATE_MULTIPLIER
        day = date % 100
        month = (date / 100) % 100
        year = date / 10000

        { "year" => year, "month" => month, "day" => day, "id" => id, "type" => TYPE_FINAL }
    end

    desc "Generate versionCode from version components"
    private_lane :generateVersionCode do |versionComponents|
        puts "Generating version code from #{versionComponents}"
        date = versionComponents["year"] * 10000 + versionComponents["month"] * 100 + versionComponents["day"]
        VERSION_CODE_OFFSET + date * DATE_MULTIPLIER + versionComponents["id"]
    end

    desc "Compute version name from version code"
    private_lane :generateVersionName do |versionComponents|
        "#{versionComponents["year"]}.#{versionComponents["month"]}.#{versionComponents["day"]}.#{versionComponents["id"]}"
    end

    desc "Read versions from gradle file"
    private_lane :getVersionInfo do
        File.open("../app/build.gradle.kts","r") do |file|
            text = file.read
            versionName = text.match(/versionName = "(.*)"$/)[1]
            versionCode = text.match(/versionCode = ([0-9]*)$/)[1].to_i

            { "versionCode" => versionCode, "versionName" => versionName }
        end
    end

    desc "Write versions to gradle file"
    private_lane :writeVersions do |options|
        File.open("../app/build.gradle.kts","r+") do |file|
            text = file.read
            text.sub!(/versionName = "(.*)"$/, "versionName = \"#{options[:versionName]}\"")
            text.sub!(/versionCode = ([0-9]*)$/, "versionCode = #{options[:versionCode]}")
            file.rewind
            file.write(text)
            file.truncate(file.pos)
        end
    end

    private_lane :incrementVersionComponents do |options|
        versionComponents = options[:versionComponents]
        today = Time.now
        todayYear = today.year % 100
        isSameDay = versionComponents["year"] == todayYear &&
            versionComponents["month"] == today.month &&
            versionComponents["day"] == today.day

        case options[:type]
        when "release"
            versionComponents["year"] = todayYear
            versionComponents["month"] = today.month
            versionComponents["day"] = today.day
            versionComponents["id"] = 0
        when "build"
            if isSameDay
                versionComponents["id"] = versionComponents["id"] + 1
            else
                versionComponents["year"] = todayYear
                versionComponents["month"] = today.month
                versionComponents["day"] = today.day
                versionComponents["id"] = 0
            end
        else
            UI.user_error!("Unknown or missing version increment type #{options[:type]}. Usage: incrementVersion type:(release|build)")
        end
        versionComponents
    end

    desc "Get tag name from version components"
    private_lane :getTagName do |versionComponents|
        "#{versionComponents["year"]}.#{versionComponents["month"]}.#{versionComponents["day"]}.#{versionComponents["id"]}"
    end

    desc "Check if version is releasable"
    private_lane :checkReleasable do |versionComponents|
        if versionComponents["type"] != TYPE_FINAL
            UI.user_error!("Version is not releasable: #{versionComponents["type"]}")
        end
    end

    desc "Get play store track from version type"
    private_lane :getPlayStoreTrack do |versionComponents|
        case versionComponents["type"]
        when TYPE_FINAL
            track = "production"
        else
            UI.user_error!("Version is not releasable: #{versionComponents["type"]}")
        end
    end
    
    desc "Run tests"
	private_lane :test do
		 gradle(task: "clean testGplayDebugUnitTest testGenericDebugUnitTest testQaDebugUnitTest")
	end
	
	desc "Build app bundle"
	private_lane :buildBundle do
		gradle(
			task: 'bundle',
			flavor: 'Gplay',
			build_type: 'Release',
			print_command: false,
			properties: GRADLE_SIGNING_PROPERTIES
			)
	end
	
	desc "Build signed APK"
	private_lane :SignedRelease do |options|
		gradle(
			task: 'assemble',
			flavor: options[:flavor],
			build_type: 'Release',
			print_command: false,
			properties: GRADLE_SIGNING_PROPERTIES
			)
			
		sh("mv ../app/build/outputs/apk/#{options[:flavor]}/release/*.apk ../release/nextcloud-#{APP}-#{options[:flavor]}-#{options[:versionCode]}.apk")
	end
	
	desc "Build all APKs"
	private_lane :buildAPKs do |options|
		SignedRelease(flavor: "generic", versionCode: options[:code])
		SignedRelease(flavor: "gplay", versionCode: options[:code])
		
		if APP == "files"
			SignedRelease(flavor: "huawei", versionCode: options[:code])
		end
	end
	
	desc "Create release tag"
        private_lane :tag do |options|
            tagName = options[:name]
            add_git_tag(
              tag: tagName,
              sign: true
            )
            push_git_tags(tag: tagName)
        end

	 desc "Upload release artifacts to Google Play"
		private_lane :uploadToPlayStore do |options|
			upload_to_play_store(
				skip_upload_images: true,
				skip_upload_apk: true,
				track: options[:track],
				aab: BUNDLE_PATH
			)
		end
		
	 desc "Check if release artifacts exist"
		private_lane :checkArtifactsExist do
			if !File.exist?("../#{BUNDLE_PATH}")
				UI.user_error!("Bundle not found at #{BUNDLE_PATH}")
			end
		end

end

private_lane :promptYesNo do |options|
    puts "\n" + options[:text]
    answer = prompt(text: "is this okay?", boolean: true)
    if !answer
        UI.user_error!("Aborting")
    end
end
