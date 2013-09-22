package com.mrzon.churchhub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mrzon.churchhub.model.Church;
import com.mrzon.churchhub.model.Country;
import com.mrzon.churchhub.model.Denomination;
import com.mrzon.churchhub.model.Helper;
import com.mrzon.churchhub.model.Worship;
import com.mrzon.churchhub.model.WorshipWeek;
import com.mrzon.churchhub.util.Util;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class CHDialog {
	public static class AddWorshipDialogFragment  extends DialogFragment {
		private Church church;
		private Worship w;
		private int day=0;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_worship, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.name);
			final EditText stime= (EditText)v.findViewById(R.id.start);
			final EditText etime = (EditText)v.findViewById(R.id.end);
			final Spinner s = (Spinner) v.findViewById(R.id.Spinner01);
			Resources res = getResources();

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
					android.R.layout.simple_spinner_item, res.getStringArray(R.array.days));
			s.setAdapter(adapter);
			if(w!=null) {
				cname.setText(w.getName());
				stime.setText(w.getStartString());
				etime.setText(w.getEndString());
				s.setSelection(w.getDay());
			}
			s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					day = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			builder.setMessage("Insert information of the worship").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String start = stime.getText().toString();
					String end = etime.getText().toString();
					try {
						Worship c = new Worship();
						c.setName(name);
						c.setChurch(church);
						if(start.split(":").length>0) {
							c.setStart(Double.parseDouble(start.split(":")[0])+Double.parseDouble(start.split(":")[1])/60);
						}
						if(end.split(":").length>0) {
							c.setEnd(Double.parseDouble(end.split(":")[0])+Double.parseDouble(end.split(":")[1])/60);
						}
						c.setDay(day);
						//TODO implement establishment date c.setEsDate(new Date(date));
						c.submit();
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Add church failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}

		public void setChurch(Church church) {
			this.church = church;
		}

		public void setWorship(Worship worship) {
			w = worship;
		}
		
		
	}
	public static class AddWorshipWeekInfoDialogFragment  extends DialogFragment {
		private WorshipWeek worshipWeek;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_worship_week_info, null);
			builder.setView(v);
			final TextView worshipName = (TextView)v.findViewById(R.id.worship_name);
			final TextView worshipTime = (TextView)v.findViewById(R.id.worship_time);
			worshipName.setText(worshipWeek.getWorship().getName());
			worshipTime.setText(worshipWeek.getTimeString());
			final EditText espeaker = (EditText)v.findViewById(R.id.speaker);
			final EditText etheme= (EditText)v.findViewById(R.id.theme);
			final EditText enats = (EditText)v.findViewById(R.id.nats);
			espeaker.setText(worshipWeek.getSpeaker());
			etheme.setText(worshipWeek.getTheme());
			enats.setText(worshipWeek.getNats());

			builder.setMessage("Insert information of the worship week").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String speaker = espeaker.getText().toString();
					String theme = etheme.getText().toString();
					String nats = enats.getText().toString();
					try {
						worshipWeek.setSpeaker(speaker);
						worshipWeek.setTheme(theme);
						worshipWeek.setNats(nats);
						worshipWeek.submit();
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Add church failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}

		public void setWorshipWeek(WorshipWeek worshipWeek) {
			this.worshipWeek = worshipWeek;
		}
	}

	public static class AttendDialogFragment extends DialogFragment {
		private WorshipWeek worship;

		public WorshipWeek getWorship() {
			return worship;
		}

		public void setWorship(WorshipWeek worship) {
			this.worship = worship;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final EditText input = new EditText(getActivity().getBaseContext());
			input.setTextColor(Color.BLACK);
			builder.setView(input);
			builder.setMessage(R.string.attend_message_dialog)
			.setPositiveButton(R.string.attend_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Helper.attend(worship,input.getText().toString());
					Toast.makeText(getActivity().getBaseContext(), "Attended", Toast.LENGTH_LONG).show();
				}
			})
			.setNegativeButton(R.string.attend_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}


	public static class LoginDialogFragment extends DialogFragment {
		private Button login;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


			LayoutInflater inflater = getActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			View v = inflater.inflate(R.layout.dialog_login, null);
			builder.setView(v);

			final EditText username = (EditText)v.findViewById(R.id.username);

			final EditText password= (EditText)v.findViewById(R.id.password);
			builder.setMessage(R.string.login_message_dialog)
			.setPositiveButton(R.string.login_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = username.getText().toString();
					String pass = password.getText().toString();
					try {
						ParseUser.logIn(name, pass);
						login.setText("Logout");
						sendMessage();
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
			})
			.setNegativeButton(R.string.login_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		public void setView(Button login) {
			this.login=login;
		}
		
		// Send an Intent with an action named "custom-event-name". The Intent sent should 
		// be received by the ReceiverActivity.
		private void sendMessage() {
		  Log.d("sender", "Broadcasting message");
		  Intent intent = new Intent(Util.LOGIN_EVENT);
		  // You can also include some extra data.
		  intent.putExtra("message", "This is my message!");
		  LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(intent);
		}
	}
	public static class SignUpDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


			LayoutInflater inflater = getActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			View v = inflater.inflate(R.layout.dialog_sign_up, null);
			builder.setView(v);

			final EditText email = (EditText)v.findViewById(R.id.email);

			final EditText username = (EditText)v.findViewById(R.id.username);

			final EditText password= (EditText)v.findViewById(R.id.password);
			builder.setMessage(R.string.signup_message_dialog)
			.setPositiveButton(R.string.signup, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String em = email.getText().toString();
					String name = username.getText().toString();
					String pass = password.getText().toString();
					ParseUser parseUser = new ParseUser();
					parseUser.setEmail(em);
					parseUser.setUsername(name);
					parseUser.setPassword(pass);
					boolean error = false;
					//					final ProgressDialog pf = ProgressDialog.show(getActivity().getBaseContext(), "Working..", "Setting up your account");
					try {
						parseUser.signUp();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						error = true;
						Toast.makeText(getActivity().getApplicationContext(), "Signup failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					if (!error) {
						Toast.makeText(getActivity().getApplicationContext(), "Registration success, please login to continue", Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(R.string.login_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}
	public static class BrowseDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.denomination_message_dialog)
			.setItems(R.array.browse_choice, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						Intent intent = new Intent(getActivity(), BrowseDenominationActivity.class);
						startActivity(intent);
						break;
					case 1:
						Intent intent2 = new Intent(getActivity(), BrowseCountryActivity.class);
						startActivity(intent2);
						break;

					}
				}
			});
			return builder.create();
		}
	}

	public static class AddDenominationDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_denomination, null);
			builder.setView(v);
			final EditText dname = (EditText)v.findViewById(R.id.name);
			final EditText abbv= (EditText)v.findViewById(R.id.abbr);
			final EditText web = (EditText)v.findViewById(R.id.website);
			final EditText wiki= (EditText)v.findViewById(R.id.wiki);

			builder.setMessage(R.string.add_denomination_message_dialog)
			.setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = dname.getText().toString();
					String abb = abbv.getText().toString();
					String we = web.getText().toString();
					String wik = wiki.getText().toString();

					try {
						Denomination d = new Denomination();
						d.setName(name);
						d.setWikilink(wik);
						d.setWebsite(we);
						d.setAbbreviation(abb);
						d.submit();
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Add denomination failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}
	}

	public static class AddChurchDialogFragment extends DialogFragment {
		private Denomination denomination;
		private String address;
		private double lat,lon;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_church, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.churchName);
			final EditText caddress= (EditText)v.findViewById(R.id.churchAddress);
			//TODO			final EditText cesdate = (EditText)v.findViewById(R.id.esDate);
			final EditText clocation = (EditText)v.findViewById(R.id.location);
			final EditText cwebsite = (EditText)v.findViewById(R.id.website);
			if(address!=null) {
				caddress.setText(address);
			}
			clocation.setText(lat+","+lon);

			builder.setMessage("Insert information of the church").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String add = caddress.getText().toString();
					String we = cwebsite.getText().toString();
					String loc = clocation.getText().toString();
					//					String date = cesdate.getText().toString();
					//TODO Add some date picker
					try {
						Church c = new Church();
						c.setName(name);
						c.setAddress(add);
						if(loc.split(",").length>0) {
							c.setLat(Double.parseDouble(loc.split(",")[0]));
							c.setLon(Double.parseDouble(loc.split(",")[1]));
						}
						//c.setEsDate(new Date(date));
						c.setWebsite(we);
						c.setDenomination(denomination);
						c.submit();
						if(getActivity() instanceof AddNewChurch) {
							getActivity().finish();
						}
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Add church failed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}

		public void setDenomination(Denomination denomination) {
			this.denomination = denomination;
		}

		public void setAddress(String address) {
			this.address = address;
		}
		public void setLatitude(double lat) {
			this.lat = lat;
		}
		public void setLongitude(double lon) {
			this.lon = lon;
		}
	}

	public static class AddCountryDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			View v = inflater.inflate(R.layout.dialog_add_country, null);
			builder.setView(v);
			final EditText cname = (EditText)v.findViewById(R.id.cname);
			final EditText ciso= (EditText)v.findViewById(R.id.ciso);
			builder.setMessage("Insert information of the country").
			setPositiveButton(R.string.add_denomination_message_p, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String name = cname.getText().toString();
					String iso = ciso.getText().toString();

					try {
						Country c = new Country();
						c.setName(name);
						c.setIso(iso);
						c.submit();
					} catch (ParseException e) {
						Toast.makeText(getActivity().getBaseContext(), "Add country succeed", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}	                   }
			})
			.setNegativeButton(R.string.add_denomination_message_n, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			return builder.create();
		}
	}
}
